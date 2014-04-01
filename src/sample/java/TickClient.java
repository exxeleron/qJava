/**
 *  Copyright (c) 2011-2014 Exxeleron GmbH
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.exxeleron.qjava.QCallbackConnection;
import com.exxeleron.qjava.QErrorMessage;
import com.exxeleron.qjava.QMessage;
import com.exxeleron.qjava.QMessagesListener;
import com.exxeleron.qjava.QTable;

public class TickClient {

    static QCallbackConnection q;

    public static void main( final String[] args ) {
        final TickClientFrame f = new TickClientFrame();
        f.setTitle("TickClient demo application");
        f.setSize(700, 500);

        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing( final WindowEvent e ) {
                if ( q != null ) {
                    try {
                        q.stopListener();
                        q.close();
                    } catch ( final IOException e1 ) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });

        f.setVisible(true);
    }

    static class Printout implements QMessagesListener {

        public void messageReceived( final QMessage message ) {
            System.out.println(Utils.resultToString(message.getData()));
        }

        public void errorReceived( final QErrorMessage message ) {
            System.err.println(Utils.resultToString(message.getCause()));
        }
    }

    static class TableFeed implements QMessagesListener {

        private final DefaultTableModel model;

        public TableFeed(final DefaultTableModel model) {
            this.model = model;
        }

        public void messageReceived( final QMessage message ) {
            final Object data = message.getData();
            if ( data instanceof Object[] ) {
                final Object[] list = ((Object[]) data);
                if ( list.length == 3 && list[0].equals("upd") && list[2] instanceof QTable ) {
                    final QTable table = (QTable) list[2];
                    for ( final QTable.Row row : table ) {
                        model.insertRow(0, row.toArray());
                    }
                }
            }
        }

        public void errorReceived( final QErrorMessage message ) {
            // ignore
        }
    }

    static class StartSubscriptionAction implements ActionListener {

        private final TickClientFrame tickClient;

        public StartSubscriptionAction(final TickClientFrame tickClientFrame) {
            this.tickClient = tickClientFrame;
        }

        public void actionPerformed( final ActionEvent e ) {
            if ( q == null ) {
                final String[] conn = tickClient.qhostTF.getText().split(":");
                q = new QCallbackConnection(conn.length >= 1 ? conn[0] : "localhost", conn.length >= 2 ? Integer.parseInt(conn[1]) : 5010, null, null);

                try {
                    q.open();
                    final QTable model = (QTable) ((Object[]) q.sync(".u.sub", tickClient.qtableTF.getText(), ""))[1];
                    tickClient.table.setModel(new DefaultTableModel(model.getColumns(), 0));

                    q.addMessagesListener(new TableFeed((DefaultTableModel) tickClient.table.getModel()));
                } catch ( final Exception e1 ) {
                    e1.printStackTrace();
                    if ( q != null ) {
                        try {
                            q.close();
                            q = null;
                        } catch ( final IOException e2 ) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
            q.startListener();
        }
    }

    static class StopSubscriptionAction implements ActionListener {

        public void actionPerformed( final ActionEvent arg0 ) {
            if ( q != null ) {
                q.stopListener();
            }
        }
    }

    static class TickClientFrame extends JFrame {
        private static final long serialVersionUID = 7271896087017080273L;

        JTextField qhostTF;
        JTextField qtableTF;
        JTable table;

        public TickClientFrame() {
            initComponents();
        }

        private void initComponents() {
            final JPanel toolboxPanel = new JPanel();
            toolboxPanel.setLayout(new FlowLayout());

            toolboxPanel.add(new JLabel("kdb+ host:"));
            qhostTF = new JTextField(15);
            qhostTF.setText("localhost:5010");
            toolboxPanel.add(qhostTF);

            toolboxPanel.add(new JLabel("kdb+ table:"));
            qtableTF = new JTextField(15);
            qtableTF.setText("trade");
            toolboxPanel.add(qtableTF);

            final JButton subscribeBtn = new JButton("Start");
            toolboxPanel.add(subscribeBtn);
            subscribeBtn.addActionListener(new StartSubscriptionAction(this));

            final JButton unsubscribeBtn = new JButton("Pause");
            toolboxPanel.add(unsubscribeBtn);
            unsubscribeBtn.addActionListener(new StopSubscriptionAction());

            table = new JTable(0, 0);
            final JScrollPane dataPanel = new JScrollPane(table);

            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(toolboxPanel, BorderLayout.NORTH);
            getContentPane().add(dataPanel, BorderLayout.CENTER);
        }
    }
}
