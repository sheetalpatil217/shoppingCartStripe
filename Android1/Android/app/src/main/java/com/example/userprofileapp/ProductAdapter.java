package com.example.userprofileapp;

import android.media.Image;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import com.example.userprofileapp.pojo.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    List<Product> productList= new ArrayList<>();
    static int counter=0;
    prodInterface object;
    List<Product> selectedProduct;
    String fragment_tag;

    public ProductAdapter(List<Product> productLists, List<Product> cartProds,prodInterface object,String tag) {

        productList = productLists;
        selectedProduct=cartProds;
        this.object=object;
        fragment_tag=tag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_row,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(fragment_tag == "PAYMENT"){
            counter=0;
            Log.d("chella","Coming after payment: "+counter);
        }
        Log.d("sheetal","products list"+ productList.toString());
        final Product product = productList.get(position);
        holder.prodName.setText(product.getProductName());
        holder.prodPrice.setText(product.getProductPrice().toString());
        Picasso.get().load(product.getImageLink()).into(holder.prodImage);
        //holder.prodImage.setImageURI(product.getProductImage());
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter=counter+1;
                selectedProduct.add(product);
                Log.d("chella","Counter: "+counter);
                Log.d("chella","Selected Products: "+selectedProduct);
                object.setCounter(counter);

            }
        });
     //   notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView prodName,prodPrice;
        ImageView prodImage;
        Button add;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            prodName =itemView.findViewById(R.id.product_name);
            prodPrice = itemView.findViewById(R.id.product_price);
            prodImage = itemView.findViewById(R.id.prodImage);
            add = itemView.findViewById(R.id.addButton);
        }
    }
    public interface prodInterface{
        public void setCounter(int counter);
    }
}
