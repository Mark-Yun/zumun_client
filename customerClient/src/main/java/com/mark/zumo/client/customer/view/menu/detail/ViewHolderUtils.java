package com.mark.zumo.client.customer.view.menu.detail;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.viewmodel.MenuDetailViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.exceptions.OnErrorNotImplementedException;

/**
 * Created by mark on 18. 5. 25.
 */
final class ViewHolderUtils {

    static final int SINGLE_SELECT_TYPE = 0;
    static final int MULTI_SELECT_TYPE = 1;

    static final int SINGLE_SELECT_RES = R.layout.card_view_menu_option_single_select;
    static final int MULTI_SELECT_RES = R.layout.card_view_menu_option_multi_select;

    private ViewHolderUtils() {
        /*Empty Body*/
    }

    static RecyclerView.ViewHolder inflate(ViewGroup parent, int viewType) {
        int resId;
        switch (viewType) {
            case SINGLE_SELECT_TYPE:
                resId = SINGLE_SELECT_RES;
                View view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
                return new SingleSelectViewHolder(view);

            case MULTI_SELECT_TYPE:
                resId = MULTI_SELECT_RES;
                view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
                return new MultiSelectViewHolder(view);
            default:
                throw new OnErrorNotImplementedException(new Throwable());
        }
    }

    static void inject(SingleSelectViewHolder viewHolder, String key,
                       List<MenuOption> menuOptionList, MenuDetailViewModel menuDetailViewModel) {
        MenuOption menuOption = menuOptionList.get(0);
        viewHolder.name.setText(key);
        viewHolder.price.setText(getPriceText(menuOption.price));
        viewHolder.value.setText(menuOption.value);
        viewHolder.itemView.setOnClickListener(v -> {
            boolean checked = viewHolder.value.isChecked();
            viewHolder.value.setChecked(!checked);
            if (checked) {
                menuDetailViewModel.selectMenuOption(menuOption);
            } else {
                menuDetailViewModel.deselectMenuOption(key);
            }
        });
    }

    @NonNull
    private static String getPriceText(final int price) {
        return price < 0 ? String.valueOf(price) : "+" + price;
    }

    static void inject(MultiSelectViewHolder viewHolder, String key,
                       List<MenuOption> menuOptionList, MenuDetailViewModel menuDetailViewModel) {
        viewHolder.name.setText(key);

        List<String> menuStringList = new ArrayList<>();
        for (MenuOption menuOption : menuOptionList) {
            menuStringList.add(getPriceText(menuOption.price) + " " + menuOption.value);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(viewHolder.itemView.getContext(), android.R.layout.simple_list_item_single_choice, menuStringList);
        viewHolder.value.setAdapter(arrayAdapter);
        viewHolder.value.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                menuDetailViewModel.selectMenuOption(menuOptionList.get(position));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                menuDetailViewModel.deselectMenuOption(key);
            }
        });
    }
}
