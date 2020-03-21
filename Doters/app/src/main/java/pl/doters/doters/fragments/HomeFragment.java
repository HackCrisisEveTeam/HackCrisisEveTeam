package pl.doters.doters.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.Objects;

import pl.doters.doters.R;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_view, container, false);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Objects.requireNonNull(getActivity()));
        String personName = "User";
        if (acct != null) {
            personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
        }

        int daysLeft = 11;
        int daysExtra = 3;

        String paragraph1 = personName + ", <b>" + daysLeft +
                " days" + "</b> left until the end of your quarantine!";

        String paragraph2 = "<b>If you stay at home " + daysExtra + " more days you'll get an amazing prize!";

        TextView introP1TextView = view.findViewById(R.id.home_intro_p1);
        introP1TextView.setText(Html.fromHtml(paragraph1));

        TextView introP2TextView = view.findViewById(R.id.home_intro_p_2);
        introP2TextView.setText(Html.fromHtml(paragraph2));


        return view;
    }
}
