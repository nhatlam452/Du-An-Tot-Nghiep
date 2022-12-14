package com.example.duantotnghiep.Contract;

import com.example.duantotnghiep.Model.Cart;
import com.example.duantotnghiep.Model.Order;
import com.example.duantotnghiep.Model.Response.CartResponse;
import com.example.duantotnghiep.Model.Response.OrderResponse;

import java.util.List;

public interface OrderContract {
    interface OrderModel{
        interface  OnOrderFinishedListener{
            void  onOrderFinished(OrderResponse orderResponse);
            void  onOrderFailure(Throwable t);
        }
        void newOrder(OnOrderFinishedListener orderFinishedListener, Order order);
        void getOrder(OnOrderFinishedListener orderFinishedListener,int userId);

        interface  OnCancelOrderListener{
            void  onCancelOrderSuccess(OrderResponse orderResponse);
            void  onCancelOrderFailure(Throwable t);
        }
        void cancelOrder(OnCancelOrderListener cancelOrderListener, int status, int orderId,String problem);
    }
    interface View{
        void onOrderSuccess(List<Order> cartList);
        void onOrderFailure(String msg);
        void onOrderResponseFail(Throwable t);
        void onCancelOrderSuccess(List<Order> cartList);
        void onCancelOrderFailure(String msg);
        void onCancelOrderResponseFail(Throwable t);
    }

    interface Presenter {
        void onNewOrder(Order order);
        void onGetOrder(int userId);
        void onCancelOrder(int status, int orderId,String problem);
    }
}
