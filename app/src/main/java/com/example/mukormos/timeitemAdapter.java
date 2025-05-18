package com.example.mukormos;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class timeitemAdapter extends RecyclerView.Adapter<timeitemAdapter.timeitemViewHolder> implements Filterable {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference myTimes = firebaseFirestore.collection("Times");

    private ArrayList<myTime> myTimesList;
    private ArrayList<myTime> myTimesListAll;
    private Context context;
    private int lastPos = -1;

    private Runnable onTimeDeletedCallback;

    public timeitemAdapter(Context context, ArrayList<myTime> myTimesList) {
        this.myTimesList = myTimesList;
        this.myTimesListAll = new ArrayList<>(myTimesList);
        this.context = context;
    }

    public timeitemAdapter(Context context, ArrayList<myTime> myTimesList, Runnable onTimeDeletedCallback) {
        this.context = context;
        this.myTimesList = myTimesList;
        this.onTimeDeletedCallback = onTimeDeletedCallback;
    }



    @NonNull
    @Override
    public timeitemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new timeitemViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull timeitemViewHolder holder, int position) {
        myTime currentTime = myTimesList.get(position);
        holder.bind(currentTime);

//        if (holder.getAdapterPosition() > lastPos) {
//            Animation animation = AnimationUtils.loadAnimation(context, R.anim.sliding_view);
//            holder.itemView.startAnimation(animation);
//            lastPos = holder.getAdapterPosition();
//        }
    }

    @Override
    public int getItemCount() {
        return myTimesList.size();
    }

    @Override
    public Filter getFilter() {
        return timeFilter;
    }

    private final Filter timeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<myTime> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(myTimesListAll);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (myTime item : myTimesListAll) {
                    if (item.getIdopont().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            myTimesList = (ArrayList<myTime>) results.values;
            notifyDataSetChanged();
        }
    };

    class timeitemViewHolder extends RecyclerView.ViewHolder {
        private final TextView idoTextView;
        private final TextView emailTextView;
        private final TextView foglalt;

        public timeitemViewHolder(@NonNull View itemView) {
            super(itemView);
            idoTextView = itemView.findViewById(R.id.idopontTEV); // Time string
            emailTextView = itemView.findViewById(R.id.emailTEV); // Email
            foglalt = itemView.findViewById(R.id.foglaltTEV);

            itemView.findViewById(R.id.torlesButton).setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // ✅ Vibrate before deletion
                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            vibrator.vibrate(150); // deprecated for older devices
                        }
                    }

                    myTime selected = myTimesList.get(position);
                    queryTimeForDelete(selected.getIdopont());
                }
            });
            itemView.findViewById(R.id.frissitesButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        myTime clicked = myTimesList.get(position);
                        Intent intent = new Intent(context, editActivity.class);
                        intent.putExtra("id", clicked.getId());
                        context.startActivity(intent);
                    }
                }
            });
            itemView.findViewById(R.id.foglalasButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        myTime clicked = myTimesList.get(position);
                        myTimes.document(clicked.getId()).update("foglalt", true);

                    }
                    ((MainActivity) context).recreate();
                }
            });
        }

        public void bind(myTime item) {
            idoTextView.setText(item.getIdopont());
            emailTextView.setText(item.getEmail());
            if(item.getFoglalt()) {
                foglalt.setText("Foglalt!");
                itemView.findViewById(R.id.foglalasButton).setVisibility(View.GONE);
            } else {
                foglalt.setText("Még szabad");
            }

        }
    }

    private void queryTimeForDelete(String idopont) {
        myTimes.whereEqualTo("idopont", idopont)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        deleteTime(document.getId(), idopont);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error querying time for delete", e));
    }

    private void deleteTime(String documentId, String idopont) {
        myTimes.document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Document deleted");

                    myTimesList.removeIf(item -> item.getIdopont().equals(idopont));

                    if (onTimeDeletedCallback != null) {
                        onTimeDeletedCallback.run();
                    }

                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error deleting document", e));
    }
}
