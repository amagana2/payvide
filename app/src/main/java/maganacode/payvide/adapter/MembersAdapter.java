package maganacode.payvide.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import maganacode.payvide.Models.GroupMembers;
import maganacode.payvide.R;

/**
 * Created by Andrew on 1/2/2017.
 * Adapter for the members
 */
public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {
    //Tag
    private static String TAG = "MembersAdapter";

    //Max amount for all seekbars.
    private final int TOTAL_AMOUNT = 100;
    private List<Integer> mAllProgress = new ArrayList<>();
    private List<GroupMembers> mMembers;

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName, mUsername, mPercent;
        private SeekBar mSeekBar;

        ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name_view_members);
            mUsername = (TextView) itemView.findViewById(R.id.username_view_members);
            mPercent = (TextView) itemView.findViewById(R.id.percent_text);
            mSeekBar = (SeekBar) itemView.findViewById(R.id.percent_seekbar);
        }
    }

    /**
     * Constructor
     *
     * @param members : The members.
     */
    public MembersAdapter(List<GroupMembers> members) {
        this.mMembers = members;
    }

    @Override
    public MembersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_members, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        GroupMembers individual = mMembers.get(position);
        holder.mName.setText(individual.getName());
        holder.mUsername.setText(individual.getUsername());

        //All SeekBars of size mMembers (members picked) should be 0% by default.
        for (int i = 0; i <= mMembers.size(); i++) {
            mAllProgress.add(0);
        }

        Log.d(TAG, "onBindViewHolder: " + mAllProgress.size());

        //SeekBar Listener
        holder.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Find out which seekbar triggered the event
                int which = whichIsIt(seekBar.getId(), progress);

                //Stored progress (where is it at...)
                int storedProgress = mAllProgress.get(which);
                /**Two cases can occur: User goes left or right with the thumb.
                 * If RIGHT, we must check how much he's allowed to go in that
                 * direction (based on other seekbars), and stop him before it's
                 * too late. If LEFT, free up the space to allow the other seekbars.
                 **/
                if (progress > storedProgress) {
                    //How much remains based on all seekbars.
                    int remaining = remaining();
                    //If progress 100%, don't let user move. (overextend)
                    if (remaining == 0) {
                        seekBar.setProgress(storedProgress);
                    } else {
                        //Progress available, check the availability of others,
                        //and let the user move as long as it's below 100% total.
                        if (storedProgress + remaining >= progress) {
                            mAllProgress.set(which, progress);
                        } else {
                            mAllProgress.set(which, storedProgress + remaining);
                        }
                    }
                } else if (progress <= storedProgress) {
                    mAllProgress.set(which, progress);
                }
            }

            private int whichIsIt(int id, int progress) {
                switch (id) {
                    case R.id.percent_seekbar:
                        holder.mPercent.setText(" " + progress + "%");
                        //This should return the SeekBars position out of however many seekbars there are.
                        return holder.getAdapterPosition();
                    default:
                        throw new IllegalStateException(
                                "There should be a seekbar with this id(" + id + ")!");
                }
            }

            private int remaining() {
                int remaining = TOTAL_AMOUNT;
                for (int i = 0; i < mAllProgress.size(); i++) {
                    remaining -= mAllProgress.get(i);
                }
                if (remaining >= 100) {
                    remaining = 100;
                } else if (remaining <= 0) {
                    remaining = 0;
                }
                return remaining;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mMembers.size();
    }

    public List<Integer> getList() {
        return this.mAllProgress;
    }
}
