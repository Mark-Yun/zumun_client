package com.mark.zumo.client.customer.view.menu.detail.fragment;

import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.customer.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 5. 27.
 */
public class MenuOptionSelectionAdapter extends BaseAdapter {
    private List<MenuOption> menuOptionList;

    private MenuOptionSelectionAdapter() {
        menuOptionList = new ArrayList<>();
    }

    void setMenuOptionList(final List<MenuOption> menuOptionList) {
        this.menuOptionList = menuOptionList;
    }

    @Override
    public int getCount() {
        return menuOptionList.size();
    }

    @Override
    public Object getItem(final int position) {
        return menuOptionList.get(position).name;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            convertView = layoutInflater.inflate(R.layout.dialog_list_view_menu_opion, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MenuOption item = (MenuOption) getItem(position);
        viewHolder.name.setText(item.name);


        return convertView;
    }

    class ViewHolder {
        View rootView;
        @BindView(R.id.name) AppCompatTextView name;

        ViewHolder(final View rootView) {
            this.rootView = rootView;
            ButterKnife.bind(this, rootView);
        }
    }
}
