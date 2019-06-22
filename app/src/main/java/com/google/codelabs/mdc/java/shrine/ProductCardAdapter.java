package com.google.codelabs.mdc.java.shrine;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.codelabs.mdc.java.shrine.network.ImageRequester;
import com.google.codelabs.mdc.java.shrine.network.ProductEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter used to show a simple grid of products.
 */
public class ProductCardAdapter extends RecyclerView.Adapter<ProductCardAdapter.ProductCardViewHolder>
        implements Filterable {

    private static final String TAG = ProductCardAdapter.class.getSimpleName();
    private List<ProductEntry> productList;
    private List<ProductEntry> filteredProducts;
    private ImageRequester imageRequester;

    ProductCardAdapter(List<ProductEntry> productList) {
        this.productList = productList;
        imageRequester = ImageRequester.getInstance();
    }

    @NonNull
    @Override
    public ProductCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shr_product_card, parent, false);

        return new ProductCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCardViewHolder holder, int position) {
        if (position < productList.size()) {
            holder.itemName.setText(productList.get(position).title);
            holder.itemprice.setText(productList.get(position).price);
//            holder.addToChart1.setBackgroundResource(ProductEntry.icon);
            imageRequester.setImageFromUrl(holder.itemImage, productList.get(position).url);

        } else
            Log.e("NO_LIST", "LIST IS EMPTY");
    }

    public void setProductList(List<ProductEntry> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    public List<ProductEntry> getProductsList() {
        return productList;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredProducts = productList;
                } else {
                    // get all ProductEntry objects that contains the query text
                    filteredProducts = ProductEntry.contains(productList, charSequence.toString());
                    Log.e(TAG, "performFiltering: " + filteredProducts.toString());
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredProducts;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                Log.e(TAG, "publishingFiltering: " + ((ArrayList<ProductEntry>) filterResults.values).toString());
                // modify the recycler view
                setProductList((ArrayList<ProductEntry>) filterResults.values);
            }
        };
    }

    class ProductCardViewHolder extends RecyclerView.ViewHolder {

        NetworkImageView itemImage;
        TextView itemName;
        TextView itemprice;


        ProductCardViewHolder(@NonNull View itemView) {
            super(itemView);
            //TODO: Find and store views from itemView
            itemImage = itemView.findViewById(R.id.product_image);
            itemName = itemView.findViewById(R.id.product_title);
            itemprice = itemView.findViewById(R.id.product_price);

        }
    }


}
