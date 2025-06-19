//package com.example.pokemonshop.utils;
//
//import android.Manifest;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.os.Build;
//
//import androidx.core.app.ActivityCompat;
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//
//import com.example.pokemonshop.R;
//
//public class NotificationHelper {
//    private static final String CHANNEL_ID = "cart_notification_channel";
//    private static int notificationId = 1;
//
//    public static void showCartBadgeNotification(Context context, int cartItemCount) {
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//
//        // Tạo NotificationChannel (bắt buộc với API 26+)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Cart Notifications",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        // Tạo Notification
//        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setContentTitle("Giỏ hàng của bạn")
//                .setContentText("Bạn có " + cartItemCount + " sản phẩm trong giỏ hàng.")
//                .setSmallIcon(R.drawable.ic_cart)  // Đặt icon nhỏ cho thông báo
//                .setAutoCancel(true)
//                .setNumber(10)
//                .build();
//
//        // Hiển thị thông báo
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        notificationManager.notify(notificationId, notification);
//
//        // Đặt badge count trên biểu tượng ứng dụng
//    }
//
//    public static void clearCartBadge(Context context) {
//        // Xóa badge count
//    }
//}
