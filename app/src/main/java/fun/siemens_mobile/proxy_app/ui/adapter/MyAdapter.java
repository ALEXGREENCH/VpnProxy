package fun.siemens_mobile.proxy_app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fun.siemens_mobile.proxy_app.R;
import fun.siemens_mobile.proxy_app.core.LocalVpnService;
import fun.siemens_mobile.proxy_app.ui.model.ProxyItem;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private final ArrayList<ProxyItem> proxyItemArrayList;
    private TextView preview;

    public MyAdapter(ArrayList<ProxyItem> proxyItemArrayList) {
        this.proxyItemArrayList = proxyItemArrayList;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_proxy_list, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ProxyItem item = proxyItemArrayList.get(position);

        holder.textView.setText(item.getName());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalVpnService.SELECTED_URL = item.getUrl();

                Toast.makeText(holder.itemView.getContext(), "Выбрано " + LocalVpnService.SELECTED_URL, Toast.LENGTH_SHORT).show();

                if (preview != null) {
                    preview.setSelected(false);
                }

                holder.textView.setSelected(true);

                preview = holder.textView;
            }
        });
    }

    @Override
    public int getItemCount() {
        return proxyItemArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        MyViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }
}

