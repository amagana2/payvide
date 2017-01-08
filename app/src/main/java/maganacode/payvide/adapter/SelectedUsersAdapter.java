package maganacode.payvide.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import maganacode.payvide.Models.SelectedUsers;
import maganacode.payvide.R;

/**
 * Created by Andrew on 12/23/2016.
 * Adapter for the groups.
 */

public class SelectedUsersAdapter extends RecyclerView.Adapter<SelectedUsersAdapter.ViewHolder> {
    private List<SelectedUsers> selectedUsers;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mGroupName, mMembers;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            mGroupName = (TextView) itemLayoutView.findViewById(R.id.group_name_textview);
            mMembers = (TextView) itemLayoutView.findViewById(R.id.members_text_view);
        }
    }

    /**
     * Constructor
     *
     * @param dataSet : The members
     */
    public SelectedUsersAdapter(List<SelectedUsers> dataSet) {
        this.selectedUsers = dataSet;
    }

    /**
     * How will this look?
     * Need to inflate the row_layout.
     **/
    @Override
    public SelectedUsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_groups, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //TODO : Possible delete if groupName works.
        SelectedUsers group = selectedUsers.get(position);
        holder.mGroupName.setText(group.getGroupName());
        holder.mMembers.setText("" + getItemCount() + " members");
    }

    @Override
    public int getItemCount() {
        return selectedUsers.size();
    }

}
