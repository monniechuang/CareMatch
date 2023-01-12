package com.example.user.carematch;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.ViewHolder>{
    private static final String TAG = "TEST";
    public List<Store> StoreList;
    public Context context;
    public StoreListAdapter(Context applicationContext, List<Store> StoreList){

        this.StoreList=StoreList;
        this.context = context;
    }
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_store, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.storeName.setText(StoreList.get(position).getStore_name());
        holder.storeAddress.setText(StoreList.get(position).getStore_address());
        holder.storePhone.setText(StoreList.get(position).getStore_phone());
        holder.storeTime.setText(StoreList.get(position).getStore_time());
        holder.storeDetails.setText(StoreList.get(position).getStore_details());
        holder.storeService.setText(StoreList.get(position).getStore_service());
        String Image=StoreList.get(position).getStore_image();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference picReference = storageReference.child("Store/"+Image);

        Glide.with(holder.storeImage.getContext())
                .using(new FirebaseImageLoader())
                .load(picReference)
                .into(holder.storeImage);

        final String store_id =StoreList.get(position).StoreId;//抓ID ,List可以替換

//        //以下為cardview按鈕監聽
//        holder.mView.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                Context context=view.getContext();
//
//                Intent intent = new Intent();
//                intent.setClass(context, StorePage.class);
//                intent.putExtra("StoreId", store_id);
//                Log.d(TAG,"Id: "+store_id);
//                context.startActivity(intent);
//
//
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return StoreList.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public TextView storeName;
        public TextView storeAddress;
        public ImageView storeImage;
        public TextView storeDetails;
        public TextView storePhone;
        public TextView storeTime;
        public TextView storeService;



        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            storeName =(TextView)mView.findViewById(R.id.store_name);
            storeAddress=(TextView)mView.findViewById(R.id.store_address);
            storeImage=(ImageView)mView.findViewById(R.id.store_image);
            storeDetails=(TextView)mView.findViewById(R.id.store_details);
            storePhone=(TextView)mView.findViewById(R.id.store_phone);
            storeTime=(TextView)mView.findViewById(R.id.store_time);
            storeService=(TextView)mView.findViewById(R.id.store_service);
        }
    }
}
