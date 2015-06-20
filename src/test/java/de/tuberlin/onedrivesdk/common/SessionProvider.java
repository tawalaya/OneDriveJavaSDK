package de.tuberlin.onedrivesdk.common;

import com.squareup.okhttp.OkHttpClient;
import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.networking.OneDriveSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by Sebastian on 07.05.2015.
 */
public class SessionProvider  {

    private final static String clientID = OneDriveCredentials.getClientId();
    private final static String clientSecret = OneDriveCredentials.getClientSecret();

    static File sessionFile = new File("testSession.ser");

    public static OneDriveSession getSession() throws IOException, OneDriveException {
        if(sessionFile.exists()){
            try {
                OneDriveSession session = OneDriveSession.readFromFile(sessionFile);
                if(session.isAuthenticated()){

                    return session;
                }
            } catch (IOException e) {

            }
        }
        return openSession();
    }

    private static OneDriveSession openSession() throws IOException, OneDriveException {
        final OneDriveSession session = OneDriveSession.initializeSession(new OkHttpClient(),clientID, clientSecret,
                null,new OneDriveScope[]{OneDriveScope.SIGNIN, OneDriveScope.OFFLINE_ACCESS, OneDriveScope.READWRITE});


        openWebpage(new URL(session.getAccessURL()));

        // JUnit Interactivit workaround ... bad but hey...
        final JFrame f = new JFrame("OAuthCode");
        final JTextField codeField = new JTextField();
        codeField.setPreferredSize(new Dimension(100, 21));
        JButton send = new JButton("send");
        f.setLayout(new FlowLayout(FlowLayout.LEFT));
        f.add(codeField);
        f.add(send);
        send.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    OneDriveSession.authorizeSession(session, codeField.getText());
                } catch (OneDriveException e1) {
                    e1.printStackTrace();
                }
                f.dispose();
            }
        });
        f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        while (!session.isAuthenticated()){
            Thread.yield();
        }

        OneDriveSession.write(session,sessionFile);

        return session;

    }

    private static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void openWebpage(URL url) {
        try {
            openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
