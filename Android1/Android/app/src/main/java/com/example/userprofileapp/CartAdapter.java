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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    List<Product> productList= new ArrayList<>();
    int counter=0;
    //prodInterface object=;
    List<Product> selectedProduct = new ArrayList<>();

    public CartAdapter(List<Product> productLists) {

        productList = productLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_row,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("sheetal","products list"+ productList.toString());
        final Product product = productList.get(position);
        holder.prodNameCart.setText(product.getProductName());
        holder.prodPriceCart.setText(product.getProductPrice().toString());
        Picasso.get().load(product.getImageLink()).into(holder.prodImageCart);
        //holder.prodImage.setImageResource(product.getProductImage());

        //   notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView prodNameCart,prodPriceCart;
        ImageView prodImageCart;
       // Button add;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            prodNameCart =itemView.findViewById(R.id.product_name_cart);
            prodPriceCart= itemView.findViewById(R.id.product_price_cart);
            prodImageCart= itemView.findViewById(R.id.prodImage_cart);
        }
    }
//    public interface prodInterface{
//        public void setCounter(int counter, List<Product> product);
//    }
}
