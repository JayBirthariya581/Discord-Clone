package com.topia.chat.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.topia.chat.Models.ModelAnn;
import com.topia.chat.R;
import com.topia.chat.databinding.CardAnnouncementBinding;
import com.topia.chat.databinding.DeleteAnnBinding;
import com.topia.chat.databinding.DeleteDialogBinding;

import java.util.ArrayList;

public class AnnAdapter extends RecyclerView.Adapter<AnnAdapter.MyViewHolder> {
    Context context;
    ArrayList<ModelAnn> anns;
    String status="notOwner";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AnnAdapter(Context context, ArrayList<ModelAnn> anns) {
        this.context = context;
        this.anns = anns;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_announcement, parent, false);
        return  new AnnAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelAnn a = anns.get(position);

        holder.binding.annBody.setText(a.getBody());
        holder.binding.annTime.setText(a.getTime());

        if(status.equals("Owner")){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    View v = LayoutInflater.from(context).inflate(R.layout.delete_ann, null);
                    DeleteAnnBinding b = DeleteAnnBinding.bind(v);
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Delete Message")
                            .setView(b.getRoot())
                            .create();
                    dialog.show();
                    b.deleteAnn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            FirebaseDatabase.getInstance().getReference("owners").child("announcements")
                                    .child(a.getAnnID()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        anns.remove(position);
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "Announcement Deleted", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            });

                        }
                    });


                    return false;
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return anns.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardAnnouncementBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = CardAnnouncementBinding.bind(itemView);
        }
    }
}
