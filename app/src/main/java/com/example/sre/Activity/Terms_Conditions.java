package com.example.sre.Activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sre.R;

public class Terms_Conditions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_terms_conditions);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tcTV = findViewById(R.id.tc);

        String tc = "Welcome to Sustainable Resource Exchange (\"App\"), a mobile application designed to promote sustainability by enabling the donation and request of reusable items such as food, clothes, books, and household goods. These Terms and Conditions (\"Terms\") govern your access to and use of the App. By installing or using the App, you agree to be bound by these Terms. If you do not agree, please do not use the App.\n\n" +

                "1. Purpose of the App\n" +
                "Sustainable Resource Exchange provides a platform where users can donate or request items that may otherwise go to waste. It encourages responsible consumption, environmental protection, and community support by connecting people who have extra resources with those in need.\n\n" +

                "2. Eligibility\n" +
                "- You must be at least 13 years old to use this App.\n" +
                "- By using the App, you confirm that you have the legal capacity to enter into this agreement.\n\n" +

                "3. User Accounts\n" +
                "- To access core features of the App, such as posting a donation or making a request, you must create an account using your email and password through Firebase Authentication.\n" +
                "- You are responsible for all activities that occur under your account and for maintaining the confidentiality of your login credentials.\n" +
                "- You agree to provide accurate and up-to-date information and update it as necessary.\n\n" +

                "4. Donations and Requests\n" +
                "- Users may upload photos and details of items they wish to donate.\n" +
                "- All donations must be legal, clean, safe, and in usable condition.\n" +
                "- Users requesting items must do so honestly and respectfully.\n" +
                "- The App does not manage physical delivery or pickup. Arrangements must be made directly between users.\n" +
                "- The App does not guarantee availability or quality of items listed.\n\n" +

                "5. Prohibited Conduct\n" +
                "Users must not:\n" +
                "- Upload or share content that is illegal, offensive, misleading, or harmful.\n" +
                "- Donate restricted or dangerous items such as weapons, hazardous materials, or expired food.\n" +
                "- Impersonate others or provide false information.\n" +
                "- Use the App for commercial purposes or spam.\n" +
                "- Attempt to hack, disrupt, or exploit the App or its users in any way.\n\n" +

                "6. Intellectual Property\n" +
                "- The App, its content, layout, graphics, and code are protected by copyright and other intellectual property laws.\n" +
                "- You may not reproduce, modify, distribute, or create derivative works from any part of the App without written permission.\n\n" +

                "7. Privacy Policy\n" +
                "- The App uses Firebase Authentication, Firestore, and Firebase Storage to manage user data securely.\n" +
                "- Basic user data (such as email and profile information) and user-uploaded content (such as donation images) are stored for account functionality and user interaction.\n" +
                "- No personal information will be shared with third parties without consent, except as required by law.\n\n" +

                "8. Limitation of Liability\n" +
                "- The App is provided \"as is\" and without warranties of any kind.\n" +
                "- The developers are not liable for any damages resulting from use of the App, including issues related to donated items, data loss, or interactions with other users.\n" +
                "- Users are responsible for their own safety and conduct when meeting or exchanging items.\n\n" +

                "9. Modifications and Updates\n" +
                "- These Terms may be updated from time to time. Changes will be communicated via the App or email.\n" +
                "- Continued use of the App after changes implies acceptance of the updated Terms.\n\n" +

                "10. Termination\n" +
                "- The App reserves the right to suspend or terminate accounts that violate these Terms or are found to be misused in any way.\n" +
                "- Users may also delete their accounts at any time by contacting support.\n\n" +

                "11. Governing Law\n" +
                "- These Terms are governed by and construed in accordance with the laws of the applicable jurisdiction.\n" +
                "- Any disputes arising under these Terms shall be resolved in the courts of the country or region where the App is operated.\n\n";

        tcTV.setText(tc);
    }
}