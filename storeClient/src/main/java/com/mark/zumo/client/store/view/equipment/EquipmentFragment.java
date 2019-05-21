package com.mark.zumo.client.store.view.equipment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.store.R;
import com.mark.zumo.client.store.view.equipment.bluetooth.bonded.BondedDeviceFragment;
import com.mark.zumo.client.store.view.equipment.bluetooth.found.FoundDeviceFragment;

import butterknife.ButterKnife;

/**
 * Created by mark on 19. 5. 20.
 */
public class EquipmentFragment extends Fragment {

    public static EquipmentFragment newInstance() {

        Bundle args = new Bundle();

        EquipmentFragment fragment = new EquipmentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equipment, container, false);
        ButterKnife.bind(this, view);
        inflateView();
        return view;
    }

    private void inflateView() {
        getFragmentManager().beginTransaction()
                .replace(R.id.found_device_fragment, FoundDeviceFragment.newInstance())
                .replace(R.id.bonded_device_fragment, BondedDeviceFragment.newInstance())
                .commit();
    }
}
