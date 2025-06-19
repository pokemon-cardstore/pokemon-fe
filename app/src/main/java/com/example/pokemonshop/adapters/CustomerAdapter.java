package com.example.pokemonshop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pokemonshop.R;
import com.example.pokemonshop.model.Customer;

import java.util.List;

public class CustomerAdapter extends ArrayAdapter<Customer> {
    public CustomerAdapter(Context context, List<Customer> customers) {
        super(context, 0, customers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Customer customer = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_customer, parent, false);
        }
        // Lookup view for data population
        TextView customerName = convertView.findViewById(R.id.customerName);
        // Populate the data into the template view using the data object
        customerName.setText(customer.getName());
        // Return the completed view to render on screen
        return convertView;
    }
}
