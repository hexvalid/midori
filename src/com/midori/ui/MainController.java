package com.midori.ui;

import com.midori.bot.Account;
import com.midori.bot.Engine;
import com.midori.bot.Rune;
import com.midori.database.DBAccTools;
import com.midori.database.DBCon;
import com.midori.database.DBSetTools;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;
import org.apache.commons.lang3.time.DateUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController implements Initializable {

    @FXML
    private TableView<Account> _atc;

    @FXML
    private TableColumn<Account, Integer> _atc_id;

    @FXML
    private HBox _aa_pane;

    @FXML
    private TableColumn<Account, String> _atc_email;

    @FXML
    private TableColumn<Account, Double> _atc_balance;

    @FXML
    private TableColumn<Account, Integer> _atc_rewardpoints;

    @FXML
    private TableColumn<Account, Integer> _atc_refferer;

    @FXML
    private TableColumn<Account, Integer> _atc_fpplayed;

    @FXML
    private TableColumn<Account, Double> _atc_fpbonusreqcompleted;

    @FXML
    private TableColumn<Account, String> _atc_fpstatus;

    @FXML
    private TableColumn<Account, String> _atc_boosts;

    @FXML
    private TableColumn<Account, String> _atc_proxy;

    @FXML
    private TableColumn<Account, String> _atc_status;

    @FXML
    private Label _acm_id;

    @FXML
    private TextField _aa_email;

    @FXML
    private PasswordField _aa_password;

    @FXML
    private TextField _aa_tfa_secret;

    @FXML
    private TextField _aa_referrer;

    @FXML
    private TextField _aa_useragent;

    @FXML
    private Button _aa_useragent_gen;

    @FXML
    private TextField _aa_language;

    @FXML
    private Button _aa_language_gen;

    @FXML
    private ComboBox<String> _aa_timeoffset;

    @FXML
    private ComboBox<String> _aa_proxy;

    @FXML
    private TextField _aa_proxy_address;

    @FXML
    private Button _aa_checkproxy;

    @FXML
    private Button _aa_addaccount;

    @FXML
    private CheckBox _aa_exitingaccount;

    @FXML
    private TextField _aa_email_auth;

    @FXML
    private Button _aa_email_auth_button;

    @FXML
    private TextField _set_proxyusername;

    @FXML
    private PasswordField _set_proxypassword;

    @FXML
    private TextField _set_anticaptchakey;

    @FXML
    private Button _set_updateproxycredentials;

    @FXML
    private Button _set_savesettings;

    @FXML
    public ListView<TextFlow> _log;

    @FXML
    public Button _acc_home;

    @FXML
    public Button _acc_roll;

    //Other vars
    static List<Account> accounts;
    private String tempProxyAddress;

    private Account aa_account;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            DBCon.Connect();
            accounts = DBAccTools.GetAllAccounts();
            DBSetTools.GetSettingsFromDB();
            Tools.UpdateProxyCredentials(DBSetTools.SET_PROXY_USERNAME, DBSetTools.SET_PROXY_PASSWORD);

            _atc_id.setCellValueFactory(d -> d.getValue().idProperty().asObject());
            _atc_email.setCellValueFactory(d -> d.getValue().emailProperty());
            _atc_balance.setCellValueFactory(d -> d.getValue().balanceProperty().asObject());
            _atc_balance.setCellFactory(tc -> new TableCell<Account, Double>() {
                @Override
                protected void updateItem(Double value, boolean empty) {
                    super.updateItem(value, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(String.format("%.8f", value));
                    }
                }
            });
            _atc_rewardpoints.setCellValueFactory(d -> d.getValue().rewardPointsProperty().asObject());
            _atc_refferer.setCellValueFactory(d -> d.getValue().referrerProperty().asObject());
            _atc_fpplayed.setCellValueFactory(d -> d.getValue().fpPlayedProperty().asObject());
            _atc_fpbonusreqcompleted.setCellValueFactory(d -> d.getValue().fpBonusReqCompletedProperty().asObject());
            _atc_fpstatus.setCellValueFactory(d -> d.getValue()._fpStatusProperty());
            _atc_boosts.setCellValueFactory(d -> d.getValue().boostsProperty());
            _atc_proxy.setCellValueFactory(d -> d.getValue().proxyProperty());
            _atc_status.setCellValueFactory(d -> d.getValue()._statusProperty());

            _atc.getItems().setAll(accounts);


            _atc.getSelectionModel().selectedItemProperty().addListener((obs, oldS, newS) -> {
                if (newS != null) {
                    _acm_id.setText(newS.idProperty().asString().get());
                }
            });


            _aa_useragent.setText(Rune.defaultUserAgent);
            _aa_language.setText(Rune.defaultAcceptLanguage);
            _aa_proxy.getItems().setAll("ON", "OFF", "DEBUG");
            _aa_proxy.getSelectionModel().select(0);
            _aa_timeoffset.getItems().setAll(Rune.timeZoneOffsetMap.keySet());
            _aa_timeoffset.getSelectionModel().select(41);


            //todo: fix later
            _aa_exitingaccount.fire();
            _aa_exitingaccount.setDisable(true);


            _set_proxyusername.setText(DBSetTools.SET_PROXY_USERNAME);
            _set_proxypassword.setText(DBSetTools.SET_PROXY_PASSWORD);
            _set_anticaptchakey.setText(DBSetTools.SET_ANTICAPTCHA_KEY);

            Log.Print(Log.t.INF, "midori v0.1.0 alpha");

            Log.Print(Log.t.WRN, "This is a testing version for development & feedback purposes");

            Log.Print(Log.t.SCS, "UI initiliazed.");


            ExecutorService exService = Executors.newSingleThreadExecutor();
            exService.submit(new Task<Void>() {
                @Override
                protected Void call() throws InterruptedException {
                    while (true) {
                        Date now = new Date();
                        for (Account a : accounts) {
                            if (a.isReadyForRoll()) {
                                a.set_FPStatus("Ready");
                            } else {
                                long diffInMillies = Math.abs(now.getTime() - DateUtils.addHours(a.lastFPDate, 1).getTime());
                                int diffSeconds = (int) (diffInMillies / 1000 % 60);
                                int diffMinutes = (int) (diffInMillies / (60 * 1000) % 60);
                                a.set_FPStatus(String.format("%02d", diffMinutes) + " minutes " + String.format("%02d", diffSeconds) + " seconds");
                            }
                        }


                        Thread.sleep(1000);
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @FXML
    void _aa_addAccount() {
        new Thread(() -> {
            boolean checkProxy = false;
            String proxy = null, proxyAddress = null;
            if (aa_account == null) {
                Platform.runLater(() -> {
                    _aa_pane.setDisable(true);
                    _aa_checkproxy.setDisable(true);
                    _aa_addaccount.setDisable(true);
                    _aa_exitingaccount.setDisable(true);
                });
                proxy = _aa_proxy.getSelectionModel().getSelectedItem();
                proxyAddress = "OFF";
                if (proxy.equals("ON")) {
                    checkProxy = Tools.CheckProxy(_aa_proxy_address.getText());
                    if (checkProxy) {
                        proxyAddress = _aa_proxy_address.getText();
                    }
                } else {
                    checkProxy = true;
                    proxyAddress = _aa_proxy.getSelectionModel().getSelectedItem();
                }
            } else {
                checkProxy = true;
            }

            if (checkProxy) {
                if (aa_account == null) {
                    aa_account = new Account(
                            _aa_email.getText(),
                            _aa_password.getText(),
                            _aa_tfa_secret.getLength() > 3,
                            _aa_tfa_secret.getText(),
                            Rune.timeZoneOffsetSolver(_aa_timeoffset.getSelectionModel().getSelectedItem()),
                            _aa_useragent.getText(),
                            _aa_language.getText(),
                            proxyAddress);
                } else {
                    Log.Print(Log.t.INF, "Bypassing email auth...");
                    try {
                        System.out.println(Engine.Open(aa_account, _aa_email_auth.getText().replaceAll("email_verify", "email_verify_conf")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Log.Print(Log.t.INF, "Logging into account...");
                    Engine.Login(aa_account);
                    Log.Print(Log.t.INF, "Homing account...");
                    Engine.Home(aa_account);
                    Log.Print(Log.t.INF, "Account adding to database...");
                    DBAccTools.InsertAccount(aa_account);
                    Log.Print(Log.t.SCS, "Account added");
                    aa_account = null;
                    Platform.runLater(() -> {
                        _aa_pane.setDisable(false);
                        _aa_checkproxy.setDisable(false);
                        _aa_addaccount.setDisable(false);
                        _aa_exitingaccount.setDisable(false);
                        _aa_email_auth.setVisible(false);
                        _aa_email_auth_button.setVisible(false);
                    });
                } catch (Exception e) {
                    if (e.getMessage().contains("email inbox for a link to auth")) {
                        Log.Print(Log.t.WRN, "Enter the email auth link");
                        Platform.runLater(() -> {
                            _aa_email_auth.setVisible(true);
                            _aa_email_auth_button.setVisible(true);
                            _aa_addaccount.setDisable(false);
                            _aa_email_auth.requestFocus();
                        });
                    } else {
                        Log.Print(Log.t.ERR, "Account cant added: " + e.getMessage());
                        aa_account = null;
                        Platform.runLater(() -> {
                            _aa_pane.setDisable(false);
                            _aa_checkproxy.setDisable(false);
                            _aa_addaccount.setDisable(false);
                            _aa_exitingaccount.setDisable(false);
                        });
                    }
                }

            } else {
                _aa_pane.setDisable(false);
                _aa_checkproxy.setDisable(false);
                _aa_addaccount.setDisable(false);
                _aa_exitingaccount.setDisable(false);
            }


        }).start();
    }

    @FXML
    void _aa_cancel() {
        aa_account = null;
        Platform.runLater(() -> {
            _aa_pane.setDisable(false);
            _aa_checkproxy.setDisable(false);
            _aa_addaccount.setDisable(false);
            _aa_exitingaccount.setDisable(false);
            _aa_email_auth.setText("");
            _aa_email_auth.setVisible(false);
            _aa_email_auth_button.setVisible(false);
        });
    }

    @FXML
    void _aa_checkProxy() {
        new Thread(() -> {
            Platform.runLater(() -> _aa_checkproxy.setDisable(true));
            Tools.CheckProxy(_aa_proxy_address.getText());
            Platform.runLater(() -> _aa_checkproxy.setDisable(false));
        }).start();
    }

    @FXML
    void _set_updateProxyCredentials() {
        Tools.UpdateProxyCredentials(_set_proxyusername.getText(), _set_proxypassword.getText());
        Log.Print(Log.t.INF, "Proxy Credentials updated");
    }

    @FXML
    void _set_saveSettings() {
        try {
            DBSetTools.SET_PROXY_USERNAME = _set_proxyusername.getText();
            DBSetTools.SET_PROXY_PASSWORD = _set_proxypassword.getText();
            DBSetTools.SET_ANTICAPTCHA_KEY = _set_anticaptchakey.getText();
            DBSetTools.SaveSettingsToDB();
            Log.Print(Log.t.SCS, "Settings saved to database");
        } catch (SQLException e) {
            Log.Print(Log.t.ERR, "Settings cannot saved to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void _acmStart() {
        _atc.getSelectionModel().getSelectedItem().startExecuter();
    }

    @FXML
    void _acmStop() {
        _atc.getSelectionModel().getSelectedItem().stopExecuter();
    }

    @FXML
    void _aa_changeProxy() {
        if (_aa_proxy.getValue().equals("ON")) {
            _aa_proxy_address.setDisable(false);
            _aa_checkproxy.setDisable(false);
            _aa_proxy_address.setText(tempProxyAddress);
        } else if (_aa_proxy.getValue().equals("DEBUG")) {
            if (_aa_proxy_address.getText().length() > 1 && !_aa_proxy_address.getText().equals("127.0.0.1:8080")) {
                tempProxyAddress = _aa_proxy_address.getText();
            }
            _aa_proxy_address.setText("127.0.0.1:8080");
            _aa_proxy_address.setDisable(true);
            _aa_checkproxy.setDisable(true);
        } else {
            if (_aa_proxy_address.getText().length() > 1 && !_aa_proxy_address.getText().equals("127.0.0.1:8080")) {
                tempProxyAddress = _aa_proxy_address.getText();
            }
            _aa_proxy_address.setText("");
            _aa_proxy_address.setDisable(true);
            _aa_checkproxy.setDisable(true);
        }
    }


    @FXML
    void _aa_changeExitingAccount() {
        if (_aa_exitingaccount.isSelected()) {
            _aa_tfa_secret.setDisable(false);
            _aa_referrer.setText("0");
            _aa_referrer.setDisable(true);
        } else {
            _aa_tfa_secret.setDisable(true);
            _aa_referrer.setDisable(false);
        }
    }

    @FXML
    void _aa_generateUserAgent() {
        _aa_useragent.setText(Rune.getRandomUserAgent());
    }

    @FXML
    void _aa_generateAcceptLanguage() {
        _aa_language.setText(Rune.getRandomAcceptLanguage());
    }
}
