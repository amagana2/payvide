package maganacode.payvide.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import maganacode.payvide.R;
import maganacode.payvide.Models.UserList;

/**
 * Created by Andrew on 12/21/2016.
 * Adapter for the users recyclerview.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<UserList> mDataSet;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mUsername, mCreate;
        CheckBox mCheckBox;

        ViewHolder(View v) {
            super(v);
            mName = (TextView) v.findViewById(R.id.name_view);
            mUsername = (TextView) v.findViewById(R.id.username_view);
            mCreate = (TextView) v.findViewById(R.id.createButton);
            mCheckBox = (CheckBox) v.findViewById(R.id.check_list_item);
        }
    }

    public UserAdapter(List<UserList> dataSet) {
        this.mDataSet = new ArrayList<>(dataSet);
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        UserList user = mDataSet.get(position);
        holder.mName.setText(user.getName());
        holder.mUsername.setText(user.getUsername());

        //checkbox safety
        holder.mCheckBox.setOnCheckedChangeListener(null);
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mDataSet.get(holder.getAdapterPosition()).setSelected(b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void animateTo(List<UserList> users) {
        applyAndAnimateRemovals(users);
        applyAndAnimateAdditions(users);
        applyAndAnimateMovedItems(users);
    }

    private void applyAndAnimateRemovals(List<UserList> newUsers) {
        for (int i = mDataSet.size() - 1; i >= 0; i--) {
            final UserList model = mDataSet.get(i);
            if (!newUsers.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<UserList> newUsers) {
        for (int i = 0, count = newUsers.size(); i < count; i++) {
            final UserList model = newUsers.get(i);
            if (!mDataSet.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<UserList> newUsers) {
        for (int toPosition = newUsers.size() - 1; toPosition >= 0; toPosition--) {
            final UserList model = newUsers.get(toPosition);
            final int fromPosition = mDataSet.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private UserList removeItem(int position) {
        final UserList model = mDataSet.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, UserList model) {
        mDataSet.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final UserList model = mDataSet.remove(fromPosition);
        mDataSet.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
