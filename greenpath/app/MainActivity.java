package org.greenpath.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.*;

public class MainActivity extends Activity {
    private static final int INK = Color.rgb(24, 36, 30);
    private static final int MUTED = Color.rgb(101, 116, 108);
    private static final int GREEN = Color.rgb(20, 107, 74);
    private static final int GREEN_DARK = Color.rgb(12, 72, 50);
    private static final int MINT = Color.rgb(228, 244, 235);
    private static final int BG = Color.rgb(247, 250, 247);
    private static final int BORDER = Color.rgb(222, 231, 225);
    private static final int WHITE = Color.WHITE;
    private static final int GOLD = Color.rgb(171, 112, 24);

    private FrameLayout root;
    private SharedPreferences prefs;
    private List<Opportunity> items;
    private List<Habit> habits;
    private List<LearningResource> learningResources;
    private final Set<String> saved = new HashSet<>();
    private final Set<String> completed = new HashSet<>();
    private final Set<String> interests = new LinkedHashSet<>();
    private final Set<String> actionTypes = new LinkedHashSet<>();
    private final Set<String> pledgedHabits = new LinkedHashSet<>();
    private String zip = "95051";
    private String ageGroup = "Middle school";
    private String activeTab = "Home";
    private String activeLearnFilter = "All";
    private boolean pendingReminderEnable = false;
    private boolean isLoggedIn = false;
    private String userEmail = "";
    private String userName = "";
    private static final int NOTIFICATION_PERMISSION_REQUEST = 4410;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(BG);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        prefs = getSharedPreferences("greenpath", MODE_PRIVATE);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            isLoggedIn = true;
            userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "";
            userName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : userEmail;
        } else {
            isLoggedIn = false;
            userEmail = "";
            userName = "";
        }
        items = DataRepository.opportunities();
        habits = HabitRepository.habits();
        learningResources = LearningRepository.resources();
        saved.addAll(prefs.getStringSet("saved", new HashSet<>()));
        completed.addAll(prefs.getStringSet("completed", new HashSet<>()));
        pledgedHabits.addAll(prefs.getStringSet("pledged_habits", new HashSet<>()));
        ReminderScheduler.createChannel(this);
        if (prefs.getBoolean("habit_reminder_enabled", false)) ReminderScheduler.schedule(this);
        root = new FrameLayout(this);
        root.setBackgroundColor(BG);
        setContentView(root);
        if (prefs.getBoolean("onboarded", false)) {
            zip = prefs.getString("zip", "95051");
            ageGroup = prefs.getString("age", "Middle school");
            interests.addAll(prefs.getStringSet("interests", new HashSet<>(Arrays.asList("Nature", "Transportation"))));
            actionTypes.addAll(prefs.getStringSet("types", new HashSet<>(Arrays.asList("Volunteer", "Projects"))));
            String requestedTab = getIntent() == null ? null : getIntent().getStringExtra("open_tab");
            showShell(requestedTab == null ? "Home" : requestedTab);
        } else showWelcome();
    }

    private void replace(View v) {
        root.removeAllViews();
        root.addView(v, new FrameLayout.LayoutParams(-1, -1));
    }

    private ScrollView scroll() {
        ScrollView s = new ScrollView(this);
        s.setFillViewport(true);
        s.setClipToPadding(false);
        s.setVerticalScrollBarEnabled(false);
        return s;
    }

    private LinearLayout column(int pad) {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(dp(pad), dp(pad), dp(pad), dp(pad));
        return l;
    }

    private TextView text(String value, int sp, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(sp);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setLineSpacing(0, 1.08f);
        return t;
    }

    private GradientDrawable bg(int color, float radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(radius));
        return g;
    }

    private GradientDrawable bgBorder(int color, int stroke, float radius) {
        GradientDrawable g = bg(color, radius);
        g.setStroke(dp(1), stroke);
        return g;
    }

    private TextView pill(String label, int background, int foreground) {
        TextView p = text(label, 11, foreground, true);
        p.setGravity(Gravity.CENTER);
        p.setBackground(bg(background, 100));
        p.setPadding(dp(10), dp(6), dp(10), dp(6));
        return p;
    }

    private TextView button(String label, boolean primary) {
        TextView b = text(label, 16, primary ? WHITE : GREEN_DARK, true);
        b.setGravity(Gravity.CENTER);
        b.setPadding(dp(18), dp(15), dp(18), dp(15));
        b.setBackground(primary ? bg(GREEN, 16) : bgBorder(WHITE, BORDER, 16));
        b.setClickable(true);
        return b;
    }

    private Space space(int h) {
        Space s = new Space(this);
        s.setLayoutParams(new LinearLayout.LayoutParams(1, dp(h)));
        return s;
    }

    private void showWelcome() {
        ScrollView s = scroll();
        LinearLayout c = column(24);
        c.setGravity(Gravity.CENTER_HORIZONTAL);
        c.setPadding(dp(24), dp(54), dp(24), dp(32));
        s.addView(c);

        TextView mark = text("↗", 34, WHITE, true);
        mark.setGravity(Gravity.CENTER);
        mark.setBackground(bg(GREEN, 24));
        c.addView(mark, new LinearLayout.LayoutParams(dp(68), dp(68)));
        c.addView(space(26));
        TextView logo = text("GreenPath", 34, GREEN_DARK, true);
        logo.setGravity(Gravity.CENTER);
        c.addView(logo);
        c.addView(space(10));
        TextView headline = text("From climate concern\nto local action.", 30, INK, true);
        headline.setGravity(Gravity.CENTER);
        c.addView(headline);
        c.addView(space(14));
        TextView sub = text("Discover projects, volunteer opportunities, funding and civic actions matched to you.", 16, MUTED, false);
        sub.setGravity(Gravity.CENTER);
        c.addView(sub, new LinearLayout.LayoutParams(-1, -2));
        c.addView(space(34));

        LinearLayout preview = column(18);
        preview.setBackground(bg(WHITE, 24));
        addMini(preview, "01", "Tell us what matters", "Choose your interests, age group and time.");
        preview.addView(divider());
        addMini(preview, "02", "Get local matches", "See your best actions ranked by fit.");
        preview.addView(divider());
        addMini(preview, "03", "Build your impact", "Save, complete and track real outcomes.");
        c.addView(preview, new LinearLayout.LayoutParams(-1, -2));
        c.addView(space(26));
        TextView go = button("Find my path   →", true);
        go.setOnClickListener(v -> showProfileStep());
        c.addView(go, new LinearLayout.LayoutParams(-1, -2));
        c.addView(space(12));
        TextView note = text("Prototype · local opportunity data is sample content", 11, MUTED, false);
        note.setGravity(Gravity.CENTER);
        c.addView(note);
        replace(s);
    }

    private void addMini(LinearLayout parent, String n, String title, String desc) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        TextView num = text(n, 12, GREEN, true);
        num.setGravity(Gravity.CENTER);
        num.setBackground(bg(MINT, 14));
        row.addView(num, new LinearLayout.LayoutParams(dp(46), dp(46)));
        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.setPadding(dp(14), 0, 0, 0);
        copy.addView(text(title, 15, INK, true));
        copy.addView(text(desc, 13, MUTED, false));
        row.addView(copy, new LinearLayout.LayoutParams(0, -2, 1));
        parent.addView(row, new LinearLayout.LayoutParams(-1, -2));
    }

    private View divider() {
        View d = new View(this);
        d.setBackgroundColor(Color.rgb(238, 243, 239));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(1));
        lp.setMargins(0, dp(14), 0, dp(14));
        d.setLayoutParams(lp);
        return d;
    }

    private void showProfileStep() {
        ScrollView s = scroll();
        LinearLayout c = column(22);
        c.setPadding(dp(22), dp(30), dp(22), dp(30));
        s.addView(c);
        c.addView(progress("1 of 2"));
        c.addView(space(22));
        c.addView(text("Start with you", 30, INK, true));
        c.addView(space(8));
        c.addView(text("A few details help GreenPath rank opportunities that actually fit.", 15, MUTED, false));
        c.addView(space(26));
        c.addView(label("Your ZIP code"));
        EditText z = new EditText(this);
        z.setText(zip);
        z.setTextSize(17);
        z.setTextColor(INK);
        z.setSingleLine(true);
        z.setInputType(InputType.TYPE_CLASS_NUMBER);
        z.setPadding(dp(16), dp(14), dp(16), dp(14));
        z.setBackground(bgBorder(WHITE, BORDER, 14));
        c.addView(z, new LinearLayout.LayoutParams(-1, -2));
        c.addView(space(22));
        c.addView(label("Age group"));
        LinearLayout ages = wrapRow();
        String[] ageOptions = {"Elementary", "Middle school", "High school", "College+"};
        for (String a : ageOptions) {
            TextView chip = selectableChip(a, a.equals(ageGroup));
            chip.setOnClickListener(v -> { ageGroup = a; showProfileStep(); });
            ages.addView(chip, chipLp());
        }
        c.addView(ages);
        c.addView(space(30));
        TextView next = button("Continue   →", true);
        next.setOnClickListener(v -> { zip = z.getText().toString().trim().isEmpty() ? "95051" : z.getText().toString().trim(); showInterestStep(); });
        c.addView(next, new LinearLayout.LayoutParams(-1, -2));
        replace(s);
    }

    private TextView progress(String value) {
        TextView p = pill(value, MINT, GREEN_DARK);
        p.setGravity(Gravity.CENTER);
        return p;
    }

    private TextView label(String s) {
        TextView t = text(s, 13, MUTED, true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.bottomMargin = dp(9);
        t.setLayoutParams(lp);
        return t;
    }

    private LinearLayout wrapRow() {
        LinearLayout r = new LinearLayout(this);
        r.setOrientation(LinearLayout.HORIZONTAL);
        r.setGravity(Gravity.START);
        return r;
    }

    private LinearLayout.LayoutParams chipLp() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
        lp.setMargins(0, 0, dp(7), 0);
        return lp;
    }

    private TextView selectableChip(String s, boolean selected) {
        TextView t = text(s, 12, selected ? WHITE : INK, true);
        t.setGravity(Gravity.CENTER);
        t.setPadding(dp(8), dp(12), dp(8), dp(12));
        t.setBackground(selected ? bg(GREEN, 12) : bgBorder(WHITE, BORDER, 12));
        return t;
    }

    private void showInterestStep() {
        if (interests.isEmpty()) interests.addAll(Arrays.asList("Nature", "Transportation"));
        if (actionTypes.isEmpty()) actionTypes.addAll(Arrays.asList("Volunteer", "Projects"));
        ScrollView s = scroll();
        LinearLayout c = column(22);
        c.setPadding(dp(22), dp(30), dp(22), dp(30));
        s.addView(c);
        c.addView(progress("2 of 2"));
        c.addView(space(22));
        c.addView(text("What moves you?", 30, INK, true));
        c.addView(space(8));
        c.addView(text("Choose as many as you like. You can change these anytime.", 15, MUTED, false));
        c.addView(space(24));
        c.addView(label("Climate interests"));
        String[][] rows = {{"Nature", "Water", "Energy"}, {"Transportation", "Waste", "Resilience"}};
        for (String[] row : rows) {
            LinearLayout r = wrapRow();
            for (String x : row) {
                TextView chip = selectableChip(x, interests.contains(x));
                chip.setOnClickListener(v -> { if (interests.contains(x)) interests.remove(x); else interests.add(x); showInterestStep(); });
                r.addView(chip, chipLp());
            }
            c.addView(r);
            c.addView(space(8));
        }
        c.addView(space(14));
        c.addView(label("What do you want to do?"));
        String[][] actRows = {{"Volunteer", "Projects", "Civic"}, {"Funding", "Learn", "Challenge"}};
        for (String[] row : actRows) {
            LinearLayout r = wrapRow();
            for (String x : row) {
                TextView chip = selectableChip(x, actionTypes.contains(x));
                chip.setOnClickListener(v -> { if (actionTypes.contains(x)) actionTypes.remove(x); else actionTypes.add(x); showInterestStep(); });
                r.addView(chip, chipLp());
            }
            c.addView(r);
            c.addView(space(8));
        }
        c.addView(space(24));
        TextView done = button("Show my GreenPath   →", true);
        done.setOnClickListener(v -> {
            prefs.edit().putBoolean("onboarded", true).putString("zip", zip).putString("age", ageGroup)
                    .putStringSet("interests", new HashSet<>(interests)).putStringSet("types", new HashSet<>(actionTypes)).apply();
            showShell("Home");
        });
        c.addView(done, new LinearLayout.LayoutParams(-1, -2));
        replace(s);
    }

    private void showShell(String tab) {
        activeTab = tab;
        LinearLayout shell = new LinearLayout(this);
        shell.setOrientation(LinearLayout.VERTICAL);
        FrameLayout content = new FrameLayout(this);
        shell.addView(content, new LinearLayout.LayoutParams(-1, 0, 1));
        View page = switch (tab) {
            case "Explore" -> explorePage();
            case "Pledge" -> pledgePage();
            case "Learn" -> learnPage();
            case "Saved" -> savedPage();
            case "Impact" -> impactPage();
            default -> homePage();
        };
        content.addView(page, new FrameLayout.LayoutParams(-1, -1));
        shell.addView(bottomNav(), new LinearLayout.LayoutParams(-1, dp(78)));
        replace(shell);
    }

    private View bottomNav() {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER);
        nav.setPadding(dp(8), dp(6), dp(8), dp(9));
        nav.setBackground(bg(WHITE, 0));
        String[][] tabs = {{"⌂", "Home"}, {"◎", "Explore"}, {"✓", "Pledge"}, {"▤", "Learn"}, {"◌", "Impact"}};
        for (String[] t : tabs) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            boolean on = t[1].equals(activeTab);
            TextView icon = text(t[0], 22, on ? GREEN : MUTED, on);
            icon.setGravity(Gravity.CENTER);
            TextView label = text(t[1], 11, on ? GREEN : MUTED, on);
            label.setGravity(Gravity.CENTER);
            item.addView(icon);
            item.addView(label);
            item.setOnClickListener(v -> showShell(t[1]));
            nav.addView(item, new LinearLayout.LayoutParams(0, -1, 1));
        }
        return nav;
    }

    private View homePage() {
        ScrollView s = scroll();
        LinearLayout c = column(18);
        c.setPadding(dp(18), dp(22), dp(18), dp(26));
        s.addView(c);
        c.addView(topBar());
        c.addView(space(24));
        c.addView(text("Good afternoon 👋", 14, MUTED, true));
        c.addView(space(3));
        c.addView(text("Your best ways to act", 27, INK, true));
        c.addView(space(16));
        c.addView(pathCard());
        c.addView(space(16));
        c.addView(homeFeatureCard("✓", "Daily pledge & habits",
                getGreenPoints() + " GreenPoints · " + getTodayDone().size() + " habits completed today", "Pledge"));
        c.addView(space(10));
        c.addView(homeFeatureCard("▤", "Learn something new",
                "Articles, climate news, case studies, videos and podcasts from trusted sources", "Learn"));
        c.addView(space(24));
        LinearLayout title = new LinearLayout(this);
        title.setOrientation(LinearLayout.HORIZONTAL);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.addView(text("Top matches", 20, INK, true), new LinearLayout.LayoutParams(0, -2, 1));
        TextView see = text("See all", 13, GREEN, true);
        see.setOnClickListener(v -> showShell("Explore"));
        title.addView(see);
        c.addView(title);
        c.addView(space(12));
        for (int i = 0; i < Math.min(3, items.size()); i++) {
            c.addView(opportunityCard(items.get(i), true));
            c.addView(space(12));
        }
        c.addView(space(8));
        c.addView(sectionBanner());
        return s;
    }

    private View topBar() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        TextView brand = text("↗  GreenPath", 19, GREEN_DARK, true);
        row.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));

        String accountText = isLoggedIn && !userName.isEmpty() ? "👤 " + userName.split(" ")[0] : "👤 Sign in";
        TextView accountPill = pill(accountText, MINT, GREEN_DARK);
        accountPill.setBackground(bgBorder(MINT, BORDER, 100));
        accountPill.setOnClickListener(v -> showAuthScreen());
        LinearLayout.LayoutParams accountLp = new LinearLayout.LayoutParams(-2, -2);
        accountLp.setMargins(0, 0, dp(6), 0);
        row.addView(accountPill, accountLp);

        TextView savedPill = pill("♡ " + saved.size(), WHITE, GREEN_DARK);
        savedPill.setBackground(bgBorder(WHITE, BORDER, 100));
        savedPill.setOnClickListener(v -> showShell("Saved"));
        LinearLayout.LayoutParams savedLp = new LinearLayout.LayoutParams(-2, -2);
        savedLp.setMargins(0, 0, dp(8), 0);
        row.addView(savedPill, savedLp);
        TextView zipPill = pill(zip, MINT, GREEN_DARK);
        zipPill.setOnClickListener(v -> showProfileStep());
        row.addView(zipPill);
        return row;
    }

    private void showAuthScreen() {
        ScrollView s = scroll();
        LinearLayout c = column(22);
        c.setPadding(dp(22), dp(30), dp(22), dp(30));
        s.addView(c);

        TextView back = text("←  Back", 14, GREEN, true);
        back.setOnClickListener(v -> showShell(activeTab));
        c.addView(back);
        c.addView(space(20));

        c.addView(text(isLoggedIn ? "Account Profile" : "Sign in to GreenPath", 30, INK, true));
        c.addView(space(8));
        c.addView(text(isLoggedIn ? "Manage your account, profile details, and cloud sync." : "Sync your GreenPoints, habits, and project plans across your devices.", 15, MUTED, false));
        c.addView(space(26));

        if (isLoggedIn) {
            LinearLayout profileCard = column(18);
            profileCard.setBackground(bgBorder(WHITE, BORDER, 20));
            profileCard.addView(text("👤 " + userName, 20, INK, true));
            profileCard.addView(space(4));
            profileCard.addView(text("✉ " + userEmail, 14, MUTED, false));
            profileCard.addView(space(12));
            profileCard.addView(pill("Active Member · Cloud Sync Enabled", MINT, GREEN_DARK), new LinearLayout.LayoutParams(-2, -2));
            c.addView(profileCard);
            c.addView(space(24));

            TextView logoutBtn = button("Sign out", false);
            logoutBtn.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                isLoggedIn = false;
                userEmail = "";
                userName = "";
                Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                showShell("Home");
            });
            c.addView(logoutBtn, new LinearLayout.LayoutParams(-1, -2));
        } else {
            LinearLayout form = column(16);
            form.setBackground(bgBorder(WHITE, BORDER, 20));

            form.addView(label("Your Name"));
            EditText nameInput = new EditText(this);
            nameInput.setHint("Reyaansh Gadia");
            nameInput.setText("Reyaansh Gadia");
            nameInput.setTextSize(16);
            nameInput.setTextColor(INK);
            nameInput.setSingleLine(true);
            nameInput.setPadding(dp(14), dp(12), dp(14), dp(12));
            nameInput.setBackground(bgBorder(Color.rgb(248, 250, 249), BORDER, 12));
            form.addView(nameInput, new LinearLayout.LayoutParams(-1, -2));
            form.addView(space(14));

            form.addView(label("Email Address"));
            EditText emailInput = new EditText(this);
            emailInput.setHint("reyaansh.gadia@gmail.com");
            emailInput.setText("reyaansh.gadia@gmail.com");
            emailInput.setTextSize(16);
            emailInput.setTextColor(INK);
            emailInput.setSingleLine(true);
            emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            emailInput.setPadding(dp(14), dp(12), dp(14), dp(12));
            emailInput.setBackground(bgBorder(Color.rgb(248, 250, 249), BORDER, 12));
            form.addView(emailInput, new LinearLayout.LayoutParams(-1, -2));
            form.addView(space(14));

            form.addView(label("Password"));
            EditText passInput = new EditText(this);
            passInput.setHint("ACTION1");
            passInput.setTextSize(16);
            passInput.setTextColor(INK);
            passInput.setSingleLine(true);
            passInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passInput.setPadding(dp(14), dp(12), dp(14), dp(12));
            passInput.setBackground(bgBorder(Color.rgb(248, 250, 249), BORDER, 12));
            form.addView(passInput, new LinearLayout.LayoutParams(-1, -2));
            form.addView(space(20));

            TextView loginBtn = button("Sign In / Register with Firebase", true);
            loginBtn.setOnClickListener(v -> {
                String name = nameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String password = passInput.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() > 0 && password.length() < 6) {
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String finalPassword = password.isEmpty() ? "password123" : password;
                final String finalName = name.isEmpty() ? "GreenPath User" : name;
                
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, finalPassword)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            isLoggedIn = true;
                            userEmail = (user != null && user.getEmail() != null) ? user.getEmail() : email;
                            userName = (user != null && user.getDisplayName() != null) ? user.getDisplayName() : finalName;
                            prefs.edit().putBoolean("is_logged_in", true).putString("user_name", userName).putString("user_email", userEmail).apply();
                            Toast.makeText(this, "Firebase Sign In Success: " + userName, Toast.LENGTH_LONG).show();
                            showShell("Home");
                        } else {
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, finalPassword)
                                .addOnCompleteListener(this, regTask -> {
                                    if (regTask.isSuccessful()) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        isLoggedIn = true;
                                        userEmail = (user != null && user.getEmail() != null) ? user.getEmail() : email;
                                        userName = finalName;
                                        prefs.edit().putBoolean("is_logged_in", true).putString("user_name", userName).putString("user_email", userEmail).apply();
                                        Toast.makeText(this, "Firebase Account Created & Signed In!", Toast.LENGTH_LONG).show();
                                        showShell("Home");
                                    } else {
                                        // Fallback to local session if Firebase is unconfigured or offline so user isn't blocked
                                        isLoggedIn = true;
                                        userEmail = email;
                                        userName = finalName;
                                        prefs.edit().putBoolean("is_logged_in", true).putString("user_name", userName).putString("user_email", userEmail).apply();
                                        Toast.makeText(this, "Signed in locally (Note: Enable Email/Password in Firebase Console)", Toast.LENGTH_LONG).show();
                                        showShell("Home");
                                    }
                                });
                        }
                    });
            });
            form.addView(loginBtn, new LinearLayout.LayoutParams(-1, -2));
            c.addView(form);
        }

        replace(s);
    }

    private View pathCard() {
        LinearLayout box = column(18);
        GradientDrawable grad = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{Color.rgb(16, 96, 66), Color.rgb(29, 125, 83)});
        grad.setCornerRadius(dp(24));
        box.setBackground(grad);
        TextView tag = pill("YOUR CLIMATE PATH", Color.argb(40, 255, 255, 255), WHITE);
        tag.setGravity(Gravity.CENTER);
        box.addView(tag, new LinearLayout.LayoutParams(-2, -2));
        box.addView(space(14));
        box.addView(text("Three steps for this month", 22, WHITE, true));
        box.addView(space(7));
        box.addView(text("Based on your interests in " + joinTwo(interests) + ".", 13, Color.rgb(220, 239, 229), false));
        box.addView(space(18));
        addPathRow(box, "1", "Start local", "Join the creek cleanup", false);
        addPathRow(box, "2", "Use your voice", "Give safe-routes feedback", false);
        addPathRow(box, "3", "Go bigger", "Explore a youth microgrant", true);
        return box;
    }

    private String joinTwo(Set<String> s) {
        if (s.isEmpty()) return "local climate action";
        List<String> x = new ArrayList<>(s);
        return x.size() == 1 ? x.get(0) : x.get(0) + " + " + x.get(1);
    }

    private void addPathRow(LinearLayout parent, String n, String t, String d, boolean last) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        TextView num = text(n, 12, GREEN_DARK, true);
        num.setGravity(Gravity.CENTER);
        num.setBackground(bg(WHITE, 18));
        row.addView(num, new LinearLayout.LayoutParams(dp(34), dp(34)));
        LinearLayout tx = new LinearLayout(this);
        tx.setOrientation(LinearLayout.VERTICAL);
        tx.setPadding(dp(12), 0, 0, 0);
        tx.addView(text(t, 13, WHITE, true));
        tx.addView(text(d, 12, Color.rgb(215, 236, 225), false));
        row.addView(tx, new LinearLayout.LayoutParams(0, -2, 1));
        parent.addView(row);
        if (!last) parent.addView(space(12));
    }

    private View opportunityCard(Opportunity o, boolean showMatch) {
        LinearLayout card = column(16);
        card.setBackground(bgBorder(WHITE, BORDER, 20));
        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        int badgeColor = o.badge.contains("DEADLINE") ? Color.rgb(255, 242, 221) : MINT;
        int badgeInk = o.badge.contains("DEADLINE") ? GOLD : GREEN_DARK;
        top.addView(pill(o.badge, badgeColor, badgeInk));
        if (showMatch) {
            TextView m = text(o.match + "% match", 12, GREEN, true);
            m.setGravity(Gravity.END);
            top.addView(m, new LinearLayout.LayoutParams(0, -2, 1));
        }
        card.addView(top);
        card.addView(space(12));
        card.addView(text(o.title, 19, INK, true));
        card.addView(space(5));
        card.addView(text(o.org + "  ·  " + o.city, 12, MUTED, false));
        card.addView(space(13));
        LinearLayout facts = new LinearLayout(this);
        facts.setOrientation(LinearLayout.HORIZONTAL);
        facts.addView(fact("◷", o.date), new LinearLayout.LayoutParams(0, -2, 1));
        facts.addView(fact("⌖", o.distance), new LinearLayout.LayoutParams(0, -2, 1));
        card.addView(facts);
        card.addView(space(15));
        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        TextView details = button("View details", true);
        details.setTextSize(13);
        details.setPadding(dp(12), dp(11), dp(12), dp(11));
        details.setOnClickListener(v -> showDetail(o));
        actions.addView(details, new LinearLayout.LayoutParams(0, -2, 1));
        TextView save = button(saved.contains(o.id) ? "♥" : "♡", false);
        save.setTextSize(22);
        save.setPadding(dp(10), dp(6), dp(10), dp(6));
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(dp(56), -2);
        slp.setMargins(dp(9), 0, 0, 0);
        actions.addView(save, slp);
        save.setOnClickListener(v -> { toggleSave(o.id); showShell(activeTab); });
        card.addView(actions);
        return card;
    }

    private View fact(String icon, String value) {
        LinearLayout r = new LinearLayout(this);
        r.setOrientation(LinearLayout.HORIZONTAL);
        r.setGravity(Gravity.CENTER_VERTICAL);
        r.addView(text(icon, 14, GREEN, true));
        TextView v = text("  " + value, 12, MUTED, false);
        r.addView(v);
        return r;
    }

    private View sectionBanner() {
        LinearLayout box = column(17);
        box.setBackground(bg(Color.rgb(240, 245, 255), 20));
        box.addView(text("💡  Have an idea of your own?", 16, Color.rgb(37, 63, 110), true));
        box.addView(space(6));
        box.addView(text("Turn a climate problem you notice into a simple project plan with goals, partners and measurable outcomes.", 13, Color.rgb(74, 91, 122), false));
        box.addView(space(13));
        TextView b = text("Project Builder  →", 13, Color.rgb(37, 63, 110), true);
        box.addView(b);
        box.setClickable(true);
        box.setOnClickListener(v -> showProjectBuilder());
        return box;
    }

    private void showProjectBuilder() {
        ScrollView s = scroll();
        LinearLayout c = column(20);
        c.setPadding(dp(18), dp(22), dp(18), dp(34));
        s.addView(c);
        TextView back = text("←  Back to Home", 14, GREEN, true);
        back.setOnClickListener(v -> showShell("Home"));
        c.addView(back);
        c.addView(space(18));
        c.addView(text("Project Builder", 27, INK, true));
        c.addView(space(6));
        c.addView(text("Turn a climate problem you notice into a simple project plan with goals, partners and measurable outcomes.", 14, MUTED, false));
        c.addView(space(20));

        LinearLayout form = column(16);
        form.setBackground(bgBorder(WHITE, BORDER, 20));
        
        form.addView(label("Project Title"));
        EditText titleInput = new EditText(this);
        titleInput.setHint("e.g. Neighborhood Rain Garden");
        titleInput.setTextSize(15);
        titleInput.setTextColor(INK);
        titleInput.setSingleLine(true);
        titleInput.setPadding(dp(14), dp(12), dp(14), dp(12));
        titleInput.setBackground(bgBorder(Color.rgb(248, 250, 249), BORDER, 12));
        form.addView(titleInput, new LinearLayout.LayoutParams(-1, -2));
        form.addView(space(12));

        form.addView(label("Problem to Address"));
        EditText probInput = new EditText(this);
        probInput.setHint("What local climate or environmental issue do you see?");
        probInput.setTextSize(15);
        probInput.setTextColor(INK);
        probInput.setPadding(dp(14), dp(12), dp(14), dp(12));
        probInput.setBackground(bgBorder(Color.rgb(248, 250, 249), BORDER, 12));
        form.addView(probInput, new LinearLayout.LayoutParams(-1, -2));
        form.addView(space(12));

        form.addView(label("Goals & Action Steps"));
        EditText goalsInput = new EditText(this);
        goalsInput.setHint("What are 2-3 specific steps you will take?");
        goalsInput.setTextSize(15);
        goalsInput.setTextColor(INK);
        goalsInput.setPadding(dp(14), dp(12), dp(14), dp(12));
        goalsInput.setBackground(bgBorder(Color.rgb(248, 250, 249), BORDER, 12));
        form.addView(goalsInput, new LinearLayout.LayoutParams(-1, -2));
        form.addView(space(12));

        form.addView(label("Key Partners"));
        EditText partnersInput = new EditText(this);
        partnersInput.setHint("Who can help? (e.g. neighbors, school club, city council)");
        partnersInput.setTextSize(15);
        partnersInput.setTextColor(INK);
        partnersInput.setPadding(dp(14), dp(12), dp(14), dp(12));
        partnersInput.setBackground(bgBorder(Color.rgb(248, 250, 249), BORDER, 12));
        form.addView(partnersInput, new LinearLayout.LayoutParams(-1, -2));
        form.addView(space(12));

        form.addView(label("Measurable Outcomes"));
        EditText outcomesInput = new EditText(this);
        outcomesInput.setHint("How will you measure success? (e.g. 50 native plants, 30 sign-ups)");
        outcomesInput.setTextSize(15);
        outcomesInput.setTextColor(INK);
        outcomesInput.setPadding(dp(14), dp(12), dp(14), dp(12));
        outcomesInput.setBackground(bgBorder(Color.rgb(248, 250, 249), BORDER, 12));
        form.addView(outcomesInput, new LinearLayout.LayoutParams(-1, -2));
        form.addView(space(18));

        TextView saveBtn = button("Save & Publish Project Plan", true);
        saveBtn.setOnClickListener(v -> {
            String t = titleInput.getText().toString().trim();
            if (t.isEmpty()) {
                Toast.makeText(this, "Please enter a project title", Toast.LENGTH_SHORT).show();
                return;
            }
            String p = probInput.getText().toString().trim();
            String g = goalsInput.getText().toString().trim();
            String pt = partnersInput.getText().toString().trim();
            String o = outcomesInput.getText().toString().trim();

            Set<String> savedProjects = new HashSet<>(prefs.getStringSet("saved_projects", new HashSet<>()));
            savedProjects.add(t + "::" + p + "::" + g + "::" + pt + "::" + o);
            prefs.edit().putStringSet("saved_projects", savedProjects).apply();

            Toast.makeText(this, "Project plan saved successfully! 🌱", Toast.LENGTH_LONG).show();
            showShell("Impact");
        });
        form.addView(saveBtn, new LinearLayout.LayoutParams(-1, -2));
        c.addView(form);

        Set<String> savedProjects = prefs.getStringSet("saved_projects", new HashSet<>());
        if (!savedProjects.isEmpty()) {
            c.addView(space(24));
            c.addView(text("Your saved project plans (" + savedProjects.size() + ")", 18, INK, true));
            c.addView(space(12));
            for (String sp : savedProjects) {
                String[] parts = sp.split("::", 5);
                if (parts.length >= 1) {
                    LinearLayout pc = column(12);
                    pc.setBackground(bgBorder(WHITE, BORDER, 16));
                    pc.addView(text("💡 " + parts[0], 16, INK, true));
                    if (parts.length >= 2 && !parts[1].isEmpty()) {
                        pc.addView(text("Problem: " + parts[1], 13, MUTED, false));
                    }
                    if (parts.length >= 3 && !parts[2].isEmpty()) {
                        pc.addView(text("Goals: " + parts[2], 13, MUTED, false));
                    }
                    c.addView(pc);
                    c.addView(space(8));
                }
            }
        }

        replace(s);
    }


    private View homeFeatureCard(String icon, String title, String subtitle, String tab) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(16), dp(15), dp(16), dp(15));
        card.setBackground(bgBorder(WHITE, BORDER, 18));
        TextView i = text(icon, 22, GREEN, true);
        i.setGravity(Gravity.CENTER);
        i.setBackground(bg(MINT, 14));
        card.addView(i, new LinearLayout.LayoutParams(dp(46), dp(46)));
        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.setPadding(dp(13), 0, dp(8), 0);
        copy.addView(text(title, 15, INK, true));
        copy.addView(text(subtitle, 12, MUTED, false));
        card.addView(copy, new LinearLayout.LayoutParams(0, -2, 1));
        card.addView(text("→", 19, GREEN, true));
        card.setClickable(true);
        card.setOnClickListener(v -> showShell(tab));
        return card;
    }

    private View pledgePage() {
        ScrollView s = scroll();
        LinearLayout c = column(18);
        c.setPadding(dp(18), dp(22), dp(18), dp(28));
        s.addView(c);
        c.addView(topBar());
        c.addView(space(22));
        c.addView(text("My climate pledge", 27, INK, true));
        c.addView(space(6));
        c.addView(text("Choose realistic daily habits, check them off, and build consistency one day at a time.", 14, MUTED, false));
        c.addView(space(18));

        boolean pledged = prefs.getBoolean("climate_pledge", false);
        LinearLayout pledge = column(18);
        pledge.setBackground(bg(GREEN_DARK, 22));
        pledge.addView(pill("GREENPATH PLEDGE", Color.argb(40, 255, 255, 255), WHITE), new LinearLayout.LayoutParams(-2, -2));
        pledge.addView(space(13));
        pledge.addView(text("I pledge to make at least one climate-friendly choice each day, learn why it matters, and keep improving one realistic step at a time.", 17, WHITE, true));
        pledge.addView(space(15));
        TextView pledgeBtn = text(pledged ? "✓  Pledge taken" : "Take the pledge", 14, pledged ? GREEN_DARK : WHITE, true);
        pledgeBtn.setGravity(Gravity.CENTER);
        pledgeBtn.setPadding(dp(14), dp(12), dp(14), dp(12));
        pledgeBtn.setBackground(bg(pledged ? Color.rgb(226, 246, 235) : Color.argb(38, 255, 255, 255), 14));
        pledgeBtn.setOnClickListener(v -> {
            prefs.edit().putBoolean("climate_pledge", !pledged).apply();
            showShell("Pledge");
        });
        pledge.addView(pledgeBtn, new LinearLayout.LayoutParams(-1, -2));
        c.addView(pledge);
        c.addView(space(16));

        LinearLayout score = column(17);
        score.setBackground(bgBorder(WHITE, BORDER, 20));
        LinearLayout scoreTop = new LinearLayout(this);
        scoreTop.setOrientation(LinearLayout.HORIZONTAL);
        scoreTop.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout left = new LinearLayout(this);
        left.setOrientation(LinearLayout.VERTICAL);
        left.addView(text(getGreenPoints() + " GreenPoints", 22, GREEN_DARK, true));
        left.addView(text(getTodayDone().size() + " completed today · " + pledgedHabits.size() + " habits selected", 12, MUTED, false));
        scoreTop.addView(left, new LinearLayout.LayoutParams(0, -2, 1));
        boolean reminderOn = prefs.getBoolean("habit_reminder_enabled", false);
        TextView bell = pill(reminderOn ? "🔔 ON" : "🔔 OFF", reminderOn ? MINT : WHITE, GREEN_DARK);
        if (!reminderOn) bell.setBackground(bgBorder(WHITE, BORDER, 100));
        bell.setOnClickListener(v -> toggleDailyReminder());
        scoreTop.addView(bell);
        score.addView(scoreTop);
        score.addView(space(8));
        score.addView(text(reminderOn ? "Daily reminder is scheduled for about 8:00 AM." : "Turn on the bell for one daily habit reminder.", 12, MUTED, false));
        c.addView(score);
        c.addView(space(18));

        Habit focus = todayFocusHabit();
        if (focus != null) {
            boolean done = getTodayDone().contains(focus.id);
            LinearLayout focusCard = column(17);
            focusCard.setBackground(bg(Color.rgb(240, 245, 255), 20));
            focusCard.addView(pill("TODAY'S FOCUS", Color.rgb(222, 233, 252), Color.rgb(37, 63, 110)), new LinearLayout.LayoutParams(-2, -2));
            focusCard.addView(space(10));
            focusCard.addView(text(focus.title, 18, Color.rgb(37, 63, 110), true));
            focusCard.addView(space(5));
            focusCard.addView(text(focus.description, 13, Color.rgb(74, 91, 122), false));
            focusCard.addView(space(12));
            TextView doneBtn = button(done ? "✓ Completed today" : "Complete today  ·  +" + focus.points + " points", !done);
            doneBtn.setEnabled(!done);
            if (!done) doneBtn.setOnClickListener(v -> completeHabit(focus));
            focusCard.addView(doneBtn, new LinearLayout.LayoutParams(-1, -2));
            c.addView(focusCard);
            c.addView(space(22));
        }

        c.addView(text("Choose your daily habits", 20, INK, true));
        c.addView(space(5));
        c.addView(text("Check the habits you want to work on. Each habit can earn points once per day.", 13, MUTED, false));
        c.addView(space(13));
        Set<String> todayDone = getTodayDone();
        for (Habit h : habits) {
            boolean selected = pledgedHabits.contains(h.id);
            LinearLayout card = column(15);
            card.setBackground(bgBorder(selected ? Color.rgb(250, 253, 251) : WHITE, selected ? Color.rgb(170, 214, 190) : BORDER, 18));
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            CheckBox cb = new CheckBox(this);
            cb.setChecked(selected);
            cb.setButtonTintList(ColorStateList.valueOf(GREEN));
            row.addView(cb);
            LinearLayout copy = new LinearLayout(this);
            copy.setOrientation(LinearLayout.VERTICAL);
            copy.setPadding(dp(6), 0, 0, 0);
            copy.addView(text(h.title, 15, INK, true));
            copy.addView(text(h.category + " · +" + h.points + " points", 11, GREEN, true));
            row.addView(copy, new LinearLayout.LayoutParams(0, -2, 1));
            card.addView(row);
            card.addView(space(6));
            card.addView(text(h.description, 12, MUTED, false));
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) pledgedHabits.add(h.id); else pledgedHabits.remove(h.id);
                prefs.edit().putStringSet("pledged_habits", new HashSet<>(pledgedHabits)).apply();
                showShell("Pledge");
            });
            if (selected) {
                card.addView(space(10));
                boolean done = todayDone.contains(h.id);
                TextView habitDone = button(done ? "✓ Done today" : "Mark done today  +" + h.points, !done);
                habitDone.setTextSize(13);
                habitDone.setEnabled(!done);
                if (!done) habitDone.setOnClickListener(v -> completeHabit(h));
                card.addView(habitDone, new LinearLayout.LayoutParams(-1, -2));
            }
            c.addView(card);
            c.addView(space(9));
        }
        return s;
    }

    private void toggleDailyReminder() {
        boolean enabled = prefs.getBoolean("habit_reminder_enabled", false);
        if (enabled) {
            prefs.edit().putBoolean("habit_reminder_enabled", false).apply();
            ReminderScheduler.cancel(this);
            Toast.makeText(this, "Daily habit reminder turned off", Toast.LENGTH_SHORT).show();
            showShell("Pledge");
            return;
        }
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            pendingReminderEnable = true;
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST);
            return;
        }
        enableDailyReminder();
    }

    private void enableDailyReminder() {
        prefs.edit().putBoolean("habit_reminder_enabled", true).apply();
        ReminderScheduler.schedule(this);
        Toast.makeText(this, "Daily habit reminder set for about 8:00 AM", Toast.LENGTH_SHORT).show();
        showShell("Pledge");
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST && pendingReminderEnable) {
            pendingReminderEnable = false;
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableDailyReminder();
            } else {
                Toast.makeText(this, "Notifications are off. You can still use pledges and points.", Toast.LENGTH_LONG).show();
                showShell("Pledge");
            }
        }
    }

    private void completeHabit(Habit habit) {
        Set<String> done = getTodayDone();
        if (done.contains(habit.id)) return;
        done.add(habit.id);
        prefs.edit()
                .putStringSet("habit_done_" + todayKey(), new HashSet<>(done))
                .putInt("green_points", getGreenPoints() + habit.points)
                .apply();
        Toast.makeText(this, "+" + habit.points + " GreenPoints", Toast.LENGTH_SHORT).show();
        showShell("Pledge");
    }

    private Set<String> getTodayDone() {
        return new HashSet<>(prefs.getStringSet("habit_done_" + todayKey(), new HashSet<>()));
    }

    private int getGreenPoints() {
        return prefs.getInt("green_points", 0);
    }

    private String todayKey() {
        Calendar c = Calendar.getInstance();
        return String.format(Locale.US, "%04d-%03d", c.get(Calendar.YEAR), c.get(Calendar.DAY_OF_YEAR));
    }

    private Habit todayFocusHabit() {
        List<Habit> choices = new ArrayList<>();
        if (!pledgedHabits.isEmpty()) {
            for (Habit h : habits) if (pledgedHabits.contains(h.id)) choices.add(h);
        }
        if (choices.isEmpty()) choices.addAll(habits);
        if (choices.isEmpty()) return null;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        return choices.get(Math.abs(day) % choices.size());
    }

    private View learnPage() {
        ScrollView s = scroll();
        LinearLayout c = column(18);
        c.setPadding(dp(18), dp(22), dp(18), dp(28));
        s.addView(c);
        c.addView(topBar());
        c.addView(space(22));
        c.addView(text("Learn & discover", 27, INK, true));
        c.addView(space(6));
        c.addView(text("A curated climate library of science, news, case studies, videos, and podcasts. Resources open at the original publisher.", 14, MUTED, false));
        c.addView(space(16));

        String[][] filters = {{"All", "Learn", "News"}, {"Case study", "Video", "Podcast"}};
        for (String[] filterRow : filters) {
            LinearLayout r = wrapRow();
            for (String f : filterRow) {
                boolean on = f.equals(activeLearnFilter);
                TextView p = pill(f, on ? GREEN : WHITE, on ? WHITE : MUTED);
                if (!on) p.setBackground(bgBorder(WHITE, BORDER, 100));
                p.setOnClickListener(v -> { activeLearnFilter = f; showShell("Learn"); });
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
                lp.setMargins(0, 0, dp(6), 0);
                r.addView(p, lp);
            }
            c.addView(r);
            c.addView(space(7));
        }
        c.addView(space(9));

        int shown = 0;
        for (LearningResource r : learningResources) {
            if (!activeLearnFilter.equals("All") && !r.type.equals(activeLearnFilter)) continue;
            c.addView(learningCard(r));
            c.addView(space(10));
            shown++;
        }
        if (shown == 0) c.addView(emptyCard("▤", "No resources in this category", "Try another library filter."));
        c.addView(space(8));
        c.addView(text("GreenPath links to original publishers so students can see the full context and source.", 11, MUTED, false));
        return s;
    }

    private View learningCard(LearningResource r) {
        LinearLayout card = column(16);
        card.setBackground(bgBorder(WHITE, BORDER, 19));
        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.addView(pill(resourceIcon(r.type) + "  " + r.type, MINT, GREEN_DARK));
        TextView topic = text(r.topic, 11, MUTED, true);
        topic.setGravity(Gravity.END);
        top.addView(topic, new LinearLayout.LayoutParams(0, -2, 1));
        card.addView(top);
        card.addView(space(11));
        card.addView(text(r.title, 18, INK, true));
        card.addView(space(4));
        card.addView(text(r.source, 12, GREEN, true));
        card.addView(space(8));
        card.addView(text(r.description, 13, MUTED, false));
        card.addView(space(13));
        TextView open = button("Open original source   ↗", false);
        open.setTextSize(13);
        open.setOnClickListener(v -> openUrl(r.url));
        card.addView(open, new LinearLayout.LayoutParams(-1, -2));
        return card;
    }

    private String resourceIcon(String type) {
        return switch (type) {
            case "News" -> "●";
            case "Case study" -> "▣";
            case "Video" -> "▶";
            case "Podcast" -> "◉";
            default -> "▤";
        };
    }

    private void openUrl(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "Could not open this source.", Toast.LENGTH_SHORT).show();
        }
    }

    private View explorePage() {
        ScrollView s = scroll();
        LinearLayout c = column(18);
        c.setPadding(dp(18), dp(22), dp(18), dp(26));
        s.addView(c);
        c.addView(topBar());
        c.addView(space(22));
        c.addView(text("Explore actions", 27, INK, true));
        c.addView(space(6));
        c.addView(text("Opportunities ranked for ZIP " + zip, 14, MUTED, false));
        c.addView(space(16));
        LinearLayout filters = wrapRow();
        for (String f : new String[]{"All", "Volunteer", "Funding", "Civic"}) {
            TextView p = pill(f, f.equals("All") ? GREEN : WHITE, f.equals("All") ? WHITE : MUTED);
            if (!f.equals("All")) p.setBackground(bgBorder(WHITE, BORDER, 100));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
            lp.setMargins(0, 0, dp(6), 0);
            filters.addView(p, lp);
        }
        c.addView(filters);
        c.addView(space(18));
        for (Opportunity o : items) {
            c.addView(opportunityCard(o, true));
            c.addView(space(12));
        }
        return s;
    }

    private View savedPage() {
        ScrollView s = scroll();
        LinearLayout c = column(18);
        c.setPadding(dp(18), dp(22), dp(18), dp(26));
        s.addView(c);
        c.addView(topBar());
        c.addView(space(16));
        TextView back = text("←  Back to Explore", 13, GREEN, true);
        back.setOnClickListener(v -> showShell("Explore"));
        c.addView(back);
        c.addView(space(14));
        c.addView(text("Saved for later", 27, INK, true));
        c.addView(space(6));
        c.addView(text(saved.size() + " opportunities saved", 14, MUTED, false));
        c.addView(space(18));
        int count = 0;
        for (Opportunity o : items) if (saved.contains(o.id)) {
            c.addView(opportunityCard(o, false));
            c.addView(space(12));
            count++;
        }
        if (count == 0) c.addView(emptyCard("♡", "Nothing saved yet", "Tap the heart on an opportunity to keep it here."));
        return s;
    }

    private View emptyCard(String icon, String title, String copy) {
        LinearLayout x = column(24);
        x.setGravity(Gravity.CENTER_HORIZONTAL);
        x.setBackground(bgBorder(WHITE, BORDER, 20));
        TextView i = text(icon, 36, GREEN, false); i.setGravity(Gravity.CENTER); x.addView(i);
        x.addView(space(10));
        TextView h = text(title, 18, INK, true); h.setGravity(Gravity.CENTER); x.addView(h);
        x.addView(space(6));
        TextView p = text(copy, 13, MUTED, false); p.setGravity(Gravity.CENTER); x.addView(p);
        return x;
    }

    private View impactPage() {
        ScrollView s = scroll();
        LinearLayout c = column(18);
        c.setPadding(dp(18), dp(22), dp(18), dp(26));
        s.addView(c);
        c.addView(topBar());
        c.addView(space(22));
        c.addView(text("Impact Passport", 27, INK, true));
        c.addView(space(6));
        c.addView(text("Track actions, daily habits, and the GreenPoints you earn.", 14, MUTED, false));
        c.addView(space(18));
        LinearLayout hero = column(20);
        hero.setBackground(bg(GREEN_DARK, 22));
        hero.addView(text("YOUR IMPACT PASSPORT", 11, Color.rgb(184, 224, 202), true));
        hero.addView(space(9));
        hero.addView(text(completed.size() + "", 44, WHITE, true));
        hero.addView(text("actions completed", 14, Color.rgb(214, 235, 224), false));
        hero.addView(space(18));
        LinearLayout stats = new LinearLayout(this); stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.addView(stat(getGreenPoints() + "", "GreenPoints"), new LinearLayout.LayoutParams(0, -2, 1));
        stats.addView(stat(getTodayDone().size() + "", "habits today"), new LinearLayout.LayoutParams(0, -2, 1));
        stats.addView(stat(pledgedHabits.size() + "", "habits pledged"), new LinearLayout.LayoutParams(0, -2, 1));
        hero.addView(stats);
        c.addView(hero);
        c.addView(space(22));
        c.addView(text("Recent outcomes", 19, INK, true));
        c.addView(space(11));
        addOutcome(c, "🌊", "Creek cleanup", "2 volunteer hours · Santa Clara");
        addOutcome(c, "🚲", "Safe-routes feedback", "1 civic action · Sunnyvale");
        addOutcome(c, "💡", "Home energy detective", "5 energy uses audited");
        c.addView(space(18));
        c.addView(text("Next badge", 19, INK, true));
        c.addView(space(10));
        LinearLayout badge = column(16); badge.setBackground(bgBorder(WHITE, BORDER, 18));
        badge.addView(text("🌱  Habit Builder", 16, INK, true));
        badge.addView(space(7));
        badge.addView(text("Complete 2 more actions or daily habits to keep building your streak.", 13, MUTED, false));
        badge.addView(space(12));
        ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        pb.setMax(5); pb.setProgress(Math.min(3, 1 + completed.size()));
        badge.addView(pb, new LinearLayout.LayoutParams(-1, dp(8)));
        c.addView(badge);
        return s;
    }

    private View stat(String n, String l) {
        LinearLayout x = new LinearLayout(this); x.setOrientation(LinearLayout.VERTICAL);
        x.addView(text(n, 18, WHITE, true));
        x.addView(text(l, 11, Color.rgb(191, 219, 203), false));
        return x;
    }

    private void addOutcome(LinearLayout c, String icon, String title, String subtitle) {
        LinearLayout r = new LinearLayout(this);
        r.setOrientation(LinearLayout.HORIZONTAL); r.setGravity(Gravity.CENTER_VERTICAL);
        r.setPadding(dp(14), dp(14), dp(14), dp(14)); r.setBackground(bg(WHITE, 16));
        TextView i = text(icon, 22, INK, false); r.addView(i);
        LinearLayout tx = new LinearLayout(this); tx.setOrientation(LinearLayout.VERTICAL); tx.setPadding(dp(12), 0, 0, 0);
        tx.addView(text(title, 14, INK, true)); tx.addView(text(subtitle, 12, MUTED, false));
        r.addView(tx, new LinearLayout.LayoutParams(0, -2, 1));
        c.addView(r); c.addView(space(8));
    }

    private void showDetail(Opportunity o) {
        ScrollView s = scroll();
        LinearLayout c = column(20);
        c.setPadding(dp(20), dp(24), dp(20), dp(34));
        s.addView(c);
        TextView back = text("←  Back", 14, GREEN, true);
        back.setOnClickListener(v -> showShell(activeTab));
        c.addView(back);
        c.addView(space(20));
        c.addView(pill(o.badge, MINT, GREEN_DARK), new LinearLayout.LayoutParams(-2, -2));
        c.addView(space(13));
        c.addView(text(o.title, 29, INK, true));
        c.addView(space(7));
        c.addView(text(o.org + "  ·  " + o.city, 14, MUTED, false));
        c.addView(space(20));

        LinearLayout score = column(16); score.setBackground(bg(Color.rgb(236, 247, 240), 18));
        score.addView(text(o.match + "% match for you", 20, GREEN_DARK, true));
        score.addView(space(5));
        score.addView(text("Matched using location, age, interests, action type and effort.", 12, MUTED, false));
        c.addView(score);
        c.addView(space(20));
        LinearLayout details = column(16); details.setBackground(bgBorder(WHITE, BORDER, 18));
        addDetailRow(details, "When", o.date);
        addDetailRow(details, "Distance", o.distance);
        addDetailRow(details, "Age", o.age);
        addDetailRow(details, "Effort", o.effort);
        addDetailRow(details, "Type", o.actionType + " · " + o.category);
        c.addView(details);
        c.addView(space(22));
        c.addView(text("What you'll do", 18, INK, true));
        c.addView(space(8));
        c.addView(text(o.description, 15, MUTED, false));
        c.addView(space(20));
        c.addView(text("Why it matters", 18, INK, true));
        c.addView(space(8));
        c.addView(text(o.why, 15, MUTED, false));
        c.addView(space(28));
        TextView act = button(completed.contains(o.id) ? "✓ Completed" : "Mark as completed", true);
        act.setOnClickListener(v -> { completed.add(o.id); prefs.edit().putStringSet("completed", new HashSet<>(completed)).apply(); showDetail(o); });
        c.addView(act, new LinearLayout.LayoutParams(-1, -2));
        c.addView(space(10));
        TextView save = button(saved.contains(o.id) ? "♥  Saved" : "♡  Save for later", false);
        save.setOnClickListener(v -> { toggleSave(o.id); showDetail(o); });
        c.addView(save, new LinearLayout.LayoutParams(-1, -2));
        replace(s);
    }

    private void addDetailRow(LinearLayout p, String k, String v) {
        LinearLayout r = new LinearLayout(this); r.setOrientation(LinearLayout.HORIZONTAL); r.setGravity(Gravity.CENTER_VERTICAL);
        r.addView(text(k, 13, MUTED, true), new LinearLayout.LayoutParams(0, -2, 1));
        TextView value = text(v, 13, INK, true); value.setGravity(Gravity.END); r.addView(value, new LinearLayout.LayoutParams(0, -2, 1));
        p.addView(r); p.addView(space(12));
    }

    private void toggleSave(String id) {
        if (saved.contains(id)) saved.remove(id); else saved.add(id);
        prefs.edit().putStringSet("saved", new HashSet<>(saved)).apply();
    }

    private int dp(float x) { return (int) (x * getResources().getDisplayMetrics().density + 0.5f); }
}
