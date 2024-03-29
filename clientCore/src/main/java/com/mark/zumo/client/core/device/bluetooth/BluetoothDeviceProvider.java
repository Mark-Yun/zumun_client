package com.mark.zumo.client.core.device.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.mark.zumo.client.core.util.context.ContextHolder;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import io.reactivex.Emitter;
import io.reactivex.Maybe;
import io.reactivex.Observable;

/**
 * Created by mark on 19. 5. 20.
 */
public enum BluetoothDeviceProvider {
    INSTANCE;

    private static final String TAG = "BluetoothDeviceProvider";

    private static final String BLUETOOTH_BOARD_UUID = "00001101-0000-1000-8000-00805f9b34fb";

    private final Set<BluetoothDeviceListener> listeners;
    private final Map<String, BluetoothDevice> bondedDevices;
    private final Map<String, BluetoothSocket> bondedSockets;

    BluetoothDeviceProvider() {
        listeners = new CopyOnWriteArraySet<>();
        bondedDevices = new ConcurrentHashMap<>();
        bondedSockets = new ConcurrentHashMap<>();

        ContextHolder.getContext().registerReceiver(createStateChangedBroadcastReceiver(), createStateChangedIntentFilter());
    }

    public Set<BluetoothDevice> getBondedDevices() {
        return new HashSet<>(bondedDevices.values());
    }

    public void addListener(final BluetoothDeviceListener listener) {
        listeners.add(listener);
    }

    public void removeListener(final BluetoothDeviceListener listener) {
        listeners.remove(listener);
    }

    public Maybe<String> sendData(final BluetoothDevice bluetoothDevice, byte[] input) {
        return Maybe.create(emitter -> {
            String address = bluetoothDevice.getAddress();
            if (!bondedSockets.containsKey(address) || bondedSockets.get(address) == null) {
                emitter.onError(new IllegalArgumentException("bluetooth device is not bonded! address=" + address));
                emitter.onComplete();
                return;
            }

            BluetoothSocket bluetoothSocket = bondedSockets.get(address);
            bluetoothSocket.getOutputStream().write(input);

            final InputStream inputStream = bluetoothSocket.getInputStream();

            byte[] readBuffer = new byte[1024];  //  수신 버퍼
            int readBufferPosition = 0;        //   버퍼 내 수신 문자 저장 위치
            int timeOut = 50;
            while (timeOut-- > 0) {
                int bytesAvailable = inputStream.available();

                if (bytesAvailable == 0) {
                    Thread.sleep(100);
                    continue;
                }

                byte[] packetBytes = new byte[bytesAvailable];
                inputStream.read(packetBytes);
                for (int i = 0; i < bytesAvailable; i++) {
                    byte b = packetBytes[i];
                    if (b == 10) {
                        byte[] encodedBytes = new byte[readBufferPosition];
                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                        final String data = new String(encodedBytes, "US-ASCII");
                        readBufferPosition = 0;
                        emitter.onSuccess(data);
                        emitter.onComplete();
                    } else {
                        readBuffer[readBufferPosition++] = b;
                    }
                }

                break;
            }
        });
    }

    public boolean isDiscovering() {
        return BluetoothAdapter.getDefaultAdapter().isDiscovering();
    }

    public boolean isEnabled() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    public Observable<BluetoothDevice> startDiscovery(final Context context) {
        return Observable.create(emitter -> {
            BroadcastReceiver broadcastReceiver = createDiscoveryBroadcastReceiver(emitter);
            IntentFilter intentFilter = createDiscoveryIntentFilter();
            context.registerReceiver(broadcastReceiver, intentFilter);
            BluetoothAdapter.getDefaultAdapter().startDiscovery();
        });
    }

    public Maybe<BluetoothDevice> connectSocket(final BluetoothDevice bluetoothDevice) {
        return Maybe.create(emitter -> {
            Log.d(TAG, "connectSocket: name=" + bluetoothDevice.getName());
            UUID uuid = UUID.fromString(BLUETOOTH_BOARD_UUID);
            BluetoothSocket bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();

            String address = bluetoothDevice.getAddress();
            Log.d(TAG, "connectSocket: create connection complete. address=" + address);
            bondedDevices.put(address, bluetoothDevice);
            bondedSockets.put(address, bluetoothSocket);
            emitter.onSuccess(bluetoothDevice);
            emitter.onComplete();
        });
    }

    public boolean isBondedDeviceAddress(final String address) {
        return bondedDevices.containsKey(address);
    }

    private IntentFilter createDiscoveryIntentFilter() {
        IntentFilter stateFilter = new IntentFilter();
        stateFilter.addAction(BluetoothDevice.ACTION_FOUND);    //기기 검색됨
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);   //기기 검색 시작
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  //기기 검색 종료
        return stateFilter;
    }

    private IntentFilter createStateChangedIntentFilter() {
        IntentFilter stateFilter = new IntentFilter();
        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //BluetoothAdapter.ACTION_STATE_CHANGED : 블루투스 상태변화 액션
        stateFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED); //연결 확인
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED); //연결 끊김 확인
        stateFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        return stateFilter;
    }

    private BroadcastReceiver createDiscoveryBroadcastReceiver(final Emitter<BluetoothDevice> emitter) {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();   //입력된 action
                Log.d(TAG, "onReceive: " + action);

                if (action == null) {
                    return;
                }

                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                switch (action) {
                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED: //블루투스 기기 검색 시작

                        break;
                    case BluetoothDevice.ACTION_FOUND:  //블루투스 기기 검색 됨, 블루투스 기기가 근처에서 검색될 때마다 수행됨
                        String deviceName = device.getName();
                        String deviceAddress = device.getAddress();
                        if (TextUtils.isEmpty(deviceAddress) || TextUtils.isEmpty(deviceName)) {
                            Log.d(TAG, "onReceive: wrong bt device. device=" + deviceName);
                            return;
                        }

                        if (bondedDevices.containsKey(deviceAddress)) {
                            Log.d(TAG, "onReceive: device was already bonded.");
                            return;
                        }

                        emitter.onNext(device);

                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:    //블루투스 기기 검색 종료
                        emitter.onComplete();
                        context.unregisterReceiver(this);
                        break;
                }

            }
        };
    }

    private BroadcastReceiver createStateChangedBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();   //입력된 action
                Log.d(TAG, "onReceive: " + action);

                if (action == null) {
                    return;
                }

                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                switch (action) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED: //블루투스의 연결 상태 변경
                        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                        notifyDeviceStateChanged(device, state);
                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:  //블루투스 기기 연결
                        notifyAclConnected(device);
                        break;

                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:   //블루투스 기기 끊어짐
                        notifyAclDisconnected(device);
                        break;

                    case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                        int prevBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.BOND_NONE);
                        int newBondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                        notifyBondStateChanged(device, prevBondState, newBondState);
                        break;

                    case BluetoothDevice.ACTION_PAIRING_REQUEST:
                        notifyParingRequested(device);
                        break;
                    default:
                        Log.e(TAG, "onReceive: unexpected action");
                        break;
                }

            }
        };
    }

    private void notifyDeviceStateChanged(final BluetoothDevice bluetoothDevice, final int state) {
        for (BluetoothDeviceListener listener : listeners) {
            try {
                listener.onDeviceStateChanged(bluetoothDevice, state);
            } catch (Exception e) {
                Log.e(TAG, "onDeviceStateChanged: ", e);
            }
        }
    }

    private void notifyAclConnected(final BluetoothDevice bluetoothDevice) {
        for (BluetoothDeviceListener listener : listeners) {
            try {
                listener.onAclConnected(bluetoothDevice);
            } catch (Exception e) {
                Log.e(TAG, "onAclConnected: ", e);
            }
        }
    }

    private void notifyBondStateChanged(final BluetoothDevice bluetoothDevice,
                                        final int prevBondState,
                                        final int newBondState) {
        for (BluetoothDeviceListener listener : listeners) {
            try {
                listener.onBondStateChanged(bluetoothDevice, prevBondState, newBondState);
            } catch (Exception e) {
                Log.e(TAG, "onBondStateChanged: ", e);
            }
        }
    }

    private void notifyAclDisconnected(final BluetoothDevice bluetoothDevice) {
        String address = bluetoothDevice.getAddress();

        bondedDevices.remove(address);
        bondedSockets.remove(address);

        for (BluetoothDeviceListener listener : listeners) {
            try {
                listener.onAclDisconnected(bluetoothDevice);
            } catch (Exception e) {
                Log.e(TAG, "onAclDisconnected: ", e);
            }
        }
    }

    private void notifyParingRequested(final BluetoothDevice bluetoothDevice) {
        for (BluetoothDeviceListener listener : listeners) {
            try {
                listener.onParingRequested(bluetoothDevice);
            } catch (Exception e) {
                Log.e(TAG, "onParingRequested: ", e);
            }
        }
    }


}
