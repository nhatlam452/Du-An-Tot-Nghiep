package com.example.duantotnghiep.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.Activities.OrderDetailActivity;
import com.example.duantotnghiep.Model.CreateOrder;
import com.example.duantotnghiep.Model.Order;
import com.example.duantotnghiep.Model.OrderHistory;
import com.example.duantotnghiep.R;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {
    private final List<Order> mList;
    private final Context context;

    public OrderHistoryAdapter(List<Order> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderHistoryAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history_layout, parent, false);
        return new OrderHistoryAdapter.OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryAdapter.OrderViewHolder holder, int position) {
        Order order = mList.get(position);
        holder.tvItemPaymentMethod.setText(order.getPaymentMethod());
        holder.tvItemOrderHistoryDate.setText(order.getCreateDate());
        holder.rltGoToOD.setOnClickListener(v -> {
            Intent i = new Intent(context, OrderDetailActivity.class);
            i.putExtra("orderDetailId", order.getOrderId());
            context.startActivity(i);
            Activity activity = (Activity) context;
            activity.overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);
        });

        holder.tvTotalOrderHistory.setText(NumberFormat.getInstance().format(order.getTotal()) + "VND");
        switch (order.getPaymentMethod()) {
            case "Thanh to??n b???ng v?? Zalo Pay":
                holder.cimgPaymentMethod.setImageResource(R.drawable.img_zalo_pay);
                break;
            case "Thanh to??n b???ng v?? Momo":
                holder.cimgPaymentMethod.setImageResource(R.drawable.img_momo);
                //Momo Api
                break;
            default:
                holder.cimgPaymentMethod.setImageResource(R.drawable.img_cod);

                break;
        }
        switch (order.getStatus()) {
            /*
             * status :
             *     * 0: ???? h???y
             * 1 : ??ang ch??? x??c nh???n
             * 2: ??ang chu???n b??? ????n h??ng
             * 3 : ??ang giao h??ng
             * 4 : ???? giao h??ng
             * 5 : h???n giao l???i
             * 6 : giao h??ng kh??ng th??nh c??ng
             * */

            case 1:
                holder.tvStatusOrder.setText("Waiting for Confirm");
                holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.zaloColor));
                break;
            case 2:
                holder.tvStatusOrder.setText("Preparing Order");
                holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.teal_700));
                break;
            case 3:
                holder.tvStatusOrder.setText("On Delivery");
                holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, com.google.android.libraries.places.R.color.quantum_orange));
                break;
            case 4:
                holder.tvStatusOrder.setText("Delivery Success");
                holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                break;
            case 5:
                holder.tvStatusOrder.setText("Re-Delivery");
                holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.momoColor));
                break;
            case 6:
                holder.tvStatusOrder.setText("Delivery Falied");
                holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, R.color.red));
                break;
            default:
                holder.tvStatusOrder.setText("Order cancel");
                holder.tvStatusOrder.setTextColor(ContextCompat.getColor(context, com.google.android.libraries.places.R.color.quantum_grey));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }


    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView cimgPaymentMethod;
        private final TextView tvItemPaymentMethod;
        private final TextView tvItemOrderHistoryDate;
        private final TextView tvTotalOrderHistory;
        private final TextView tvStatusOrder;
        private final RelativeLayout rltGoToOD;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            cimgPaymentMethod = itemView.findViewById(R.id.cimgPaymentMethod);
            rltGoToOD = itemView.findViewById(R.id.rltGoToOD);
            tvItemPaymentMethod = itemView.findViewById(R.id.tvItemPaymentMethod);
            tvItemOrderHistoryDate = itemView.findViewById(R.id.tvItemOrderHistoryDate);
            tvTotalOrderHistory = itemView.findViewById(R.id.tvTotalOrderHistory);
            tvStatusOrder = itemView.findViewById(R.id.tvStatusOrder);
        }
    }
}
