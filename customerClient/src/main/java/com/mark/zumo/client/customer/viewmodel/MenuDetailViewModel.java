package com.mark.zumo.client.customer.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.customer.model.MenuManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 5. 24.
 */
public class MenuDetailViewModel extends AndroidViewModel {

    private MenuManager menuManager;
    private Map<String, List<MenuOption>> menuOptionMap;
    private Map<String, MenuOption> selectedOptionMap;

    public MenuDetailViewModel(@NonNull final Application application) {
        super(application);
        menuManager = MenuManager.INSTANCE;
        menuOptionMap = new LinkedHashMap<>();
        selectedOptionMap = new HashMap<>();
    }

    public LiveData<Menu> getMenu(String uuid) {
        MutableLiveData<Menu> liveData = new MutableLiveData<>();
        menuManager.getMenu(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(liveData::setValue);
        return liveData;
    }

    public LiveData<Map<String, List<MenuOption>>> getMenuOptionMap(String menuUuid) {
        MutableLiveData<Map<String, List<MenuOption>>> liveData = new MutableLiveData<>();
        loadMenuOptions(liveData, menuUuid);
        return liveData;
    }

    private void loadMenuOptions(MutableLiveData<Map<String, List<MenuOption>>> liveData, String menuUuid) {
        menuOptionMap.clear();
//        menuOptionMap = new LinkedHashMap<>();
        selectedOptionMap.clear();
//        selectedOptionMap = new HashMap<>();

        menuManager.getMenuOptions(menuUuid)
                .subscribeOn(Schedulers.computation())
                .flatMapSingle(Observable::toList)
                .subscribe(menuOptions -> menuOptionMap.put(menuOptions.get(0).name, menuOptions),
                        throwable -> Log.e("MenuDetailViewModel", "loadMenuOptions: ", throwable),
                        () -> liveData.postValue(menuOptionMap));
    }

    public void selectMenuOption(MenuOption menuOption) {
        selectedOptionMap.put(menuOption.name, menuOption);
    }

    public void deselectMenuOption(String key) {
        selectedOptionMap.remove(key);
    }
}
