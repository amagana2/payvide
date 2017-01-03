package maganacode.payvide.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import maganacode.payvide.Models.Group;
import maganacode.payvide.R;

/**
 * Created by Andrew on 12/23/2016.
 * Adapter for the groups.
 */

public class GroupActivityAdapter extends RecyclerView.Adapter<GroupActivityAdapter.ViewHolder> {
    private List<Group> groups;
    private String id;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mGroupName, mMembers;

        ViewHolder(View v) {
            super(v);
            mGroupName = (TextView) v.findViewById(R.id.group_name_textview);
            mMembers = (TextView) v.findViewById(R.id.members_text_view);
        }
    }

    /**
     * Constructor
     *
     * @param dataSet : The members
     * @param id   : Identifier
     */
    public GroupActivityAdapter(List<Group> dataSet, String id) {
        this.groups = dataSet;
        this.id = id;
    }

    /**
     * How will this look?
     * Need to inflate the row_layout.
     **/
    @Override
    public GroupActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_groups, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.mGroupName.setText(group.getName()); //TODO: Group Name
        holder.mMembers.setText(group.getMembers().size() + " members");
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

}
