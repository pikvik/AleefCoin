import React from 'react';
import { NavLink } from 'react-router-dom';
import { API_BASE_URL } from '../Common/apiUrl';
import { CopyToClipboard } from 'react-copy-to-clipboard';
import axios from 'axios';
import Notifications, { notify } from 'react-notify-toast';
import validator from 'validator';
import { ScaleLoader } from 'react-spinners';
import moment from 'moment';
import ToggleMenu from '../Common/togglemenu';

class UserDashboard extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            toAddress: "",
            amount: "",
            oldPassword: "",
            password: "",
            confirmPassword: "",
            sessionId: '',
            errors: { oldPassword: '', password: '', confirmPassword: '', emailId: '' },
            oldPasswordValid: false,
            passwordValid: false,
            confirmPasswordValid: false,
            loading: false,
            ethBalance: '',
            tokenBalance: '',
            tokens: 0,
            ethvalpwd: '',
            amount2: "",
            purEthvalpwd: '',
            purToAddress: '',
            purAmount: '',
            emailId: '',
            //referralPoints: sessionInfo.referralTokens,
            recTrans: [],
            copied: false,
            refValue: 'ddfsdfsdf',
            purToAddress: "",
            purEthvalpwd: "",
            purAmount: "",
            // errors: {}
            formValid: false,
            sessionInfo: [],
            secretPin: '',
            isRefBouns: false,
            refBouns: {},
            saleOffer: ''
        }
        if (sessionStorage.getItem('loginInfo') == null) {
            props.history.push('/login');
        }
        this.logOut = this.logOut.bind(this);
        this.toggleResetPwd = this.toggleResetPwd.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleChange1 = this.handleChange1.bind(this);
        this.cointransChange = this.cointransChange.bind(this);
        this.getTokenBalnce = this.getTokenBalnce.bind(this);
        this.getEtherBalnce = this.getEtherBalnce.bind(this);
        this.toggleValidate = this.toggleValidate.bind(this);
        this.toggleTransToken = this.toggleTransToken.bind(this);
        this.togglePurchase = this.togglePurchase.bind(this);
        this.recentTransaction = this.recentTransaction.bind(this);
        this.copyClip = this.copyClip.bind(this);
        this.validateForm = this.validateForm.bind(this);
        this.refLinkclick = this.refLinkclick.bind(this);
        this.getRefferalBouns = this.getRefferalBouns.bind(this);
        this.preSaleOffer = this.preSaleOffer.bind(this);
    }
    componentDidMount() {
        if (sessionStorage.getItem('loginInfo') != null) {
            let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
            this.getEtherBalnce(sessionInfo);
            this.getTokenBalnce(sessionInfo.sessionId);
            this.recentTransaction(sessionInfo);
            this.getRefferalBouns();
        }
        axios.get('https://api.coinbase.com/v2/prices/ETH-USD/spot', )
            .then(res => {
                this.setState({ ethValue: parseInt(res.data.data.amount) })
            })
        this.preSaleOffer();
    }
    cointransChange(event) {
        event.preventDefault();
        let errors = {};
        if (event.target.name == "purToAddress") {
            if (!validator.isAlphanumeric(event.target.value) && validator.isEmpty(event.target.value)) {
                errors.purToAddress = "Please Enter Valid Address";
            }

        }
        if (event.target.name == "purAmount") {
            if (!validator.isNumeric(event.target.value)) {
                errors.purAmount = "Please Enter Valid Amount";
            }
        }
        if (event.target.name == "purEthvalpwd") {
            if (!(event.target.value.length > 7)) {
                errors.purEthvalpwd = "Please Enter Valid Password";
            }
        }
        this.setState({ [event.target.name]: event.target.value })
        this.setState({ errors });
    }
    preSaleOffer() {
        let dt = new Date();
        if (moment(dt).format('L') < '08/25/2018') {
            this.setState({ saleOffer: 0.25 })
        }
        else if ('08/24/2018' < moment(dt).format('L') > '08/19/2018') {
            this.setState({ saleOffer: 0.50 })
        }
        else if ('08/18/2018' < moment(dt).format('L') > '09/08/2018') {
            this.setState({ saleOffer: 0.70 })
        }
        else if ('09/07/2018' < moment(dt).format('L') > '09/23/2018') {
            this.setState({ saleOffer: 0.90 })
        }
        else if ('09/22/2018' < moment(dt).format('L') > '10/05/2018') {
            this.setState({ saleOffer: 1.0 })
        }
        else if ('10/04/2018' < moment(dt).format('L') > '10/15/2018') {
            this.setState({ saleOffer: 1.1 })
        }
        else if ('10/14/2018' < moment(dt).format('L') > '10/23/2018') {
            this.setState({ saleOffer: 1.2 })
        }
    }
    refLinkclick() {
        if (sessionStorage.getItem('loginInfo') != null) {
            let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
            let payload = {
                'sessionId': sessionInfo.sessionId,
                'emailId': this.state.emailId
            }
            this.setState({ loading: true })
            const apiBaseUrl = API_BASE_URL + "referral";
            axios.post(apiBaseUrl, payload)
                .then(Response => {
                    this.setState({ loading: false })
                    if (Response.status == '200') {
                        notify.show(Response.data.message, 'success')
                    } else if (Response.status == 206) {
                        notify.show(Response.data.message, 'error')
                        if (Response.data.message == 'Session Expired') {
                            this.props.history.push('/login');
                            notify.show(Response.data.message, 'error')
                        }
                    }
                });
            this.setState({ emailId: '' })
        }
    }
    getRefferalBouns() {
        if (sessionStorage.getItem('loginInfo') != null) {
            let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
            let payload = {
                'sessionId': sessionInfo.sessionId
            }
            this.setState({ loading: true })
            const apiBaseUrl = API_BASE_URL + "refferal/points/list";
            axios.post(apiBaseUrl, payload)
                .then(Response => {
                    this.setState({ loading: false })
                    if (Response.status == '200') {
                        this.setState({ refBouns: Response.data.referralTokens })
                    } else if (Response.status == 206) {
                        if (Response.data.message == 'Session Expired') {
                            this.props.history.push('/login');
                            notify.show(Response.data.message, 'error')
                        }
                    }
                });
            this.setState({ emailId: '' })
        }
    }
    recentTransaction(sessionInfo) {
        const payload = {
            "sessionId": sessionInfo.sessionId,
            "etherWalletAddress": sessionInfo.etherWalletAddress,
            "transactionType": 3
        }
        const apiBaseUrl = API_BASE_URL + "token/transactionHistory";
        axios.post(apiBaseUrl, payload)
            .then(response => {
                if (response.status === 200) {
                    this.setState({ recTrans: response.data.transactionHistoryInfo });
                } else if (response.data.message === 'Session expired!') {
                    sessionStorage.removeItem('userData');
                    this.props.history.push('/login');
                    let myColor = { background: 'red', text: '#FFFFFF' };
                    notify.show(response.data.message, 'custom', 5000, myColor);
                }
            })
    }

    togglePurchase() {
        let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
        const payload = {
            "sessionId": sessionInfo.sessionId,
            "etherWalletPassword": this.state.ethvalpwd,
            "requestTokens": this.state.tokens
        }
        this.setState({ loading: true });
        const tokTransUrl = API_BASE_URL + "token/purchase";
        axios.post(tokTransUrl, payload)
            .then(response => {
                this.setState({ loading: false });
                if (response.status == 200) {
                    notify.show(response.data.message, "success");
                }
                else if (response.status == 206) {
                    notify.show(response.data.message, "error");
                }
                else if (response.data.message == 'Session Expired') {
                    this.props.history.push('/login');
                    notify.show(response.data.message, "error");
                }
            })
            .catch(function (error) {
                // console.log(error);
            });
        this.setState({ ethvalpwd: "", tokens: "" })
    }
    handleChange1(e) {
        const value = e.target.value;
        const name = e.target.name;
        this.setState({ [name]: value },
            () => { this.validateField1(name, value) });
    }
    validateField1(fieldName, value) {
        let fieldValidationErrors = this.state.errors;
        let confirmPasswordValid = this.state.confirmPasswordValid;
        let passwordValid = this.state.passwordValid;

        if (fieldName === 'oldPassword') {
            fieldValidationErrors.oldPassword = value.length > 7 ? '' : 'Must contain minimum 8 character';
        }
        if (fieldName === 'password') {
            fieldValidationErrors.password = value.length > 7 ? '' : 'Must contain minimum 8 character';
            if (value != this.state.password) {
                fieldValidationErrors.confirmPassword = 'Password does not match';
            } else if (this.state.confirmPassword != "") {

                if (value != this.state.confirmPassword) {
                    fieldValidationErrors.confirmPassword = 'Password does not match';
                } else {
                    fieldValidationErrors.confirmPassword = '';
                }
            }
        } else if (fieldName === 'confirmPassword') {
            if (value != this.state.password) {
                fieldValidationErrors.confirmPassword = 'Password does not match';
            } else {
                fieldValidationErrors.confirmPassword = '';

            }
        }

        this.setState({
            errors: fieldValidationErrors,
            confirmPasswordValid: confirmPasswordValid,
            passwordValid: passwordValid
        }, this.validateForm);
    }

    toggleTransToken() {
        let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
        const payload = {
            "sessionId": sessionInfo.sessionId,
            "toAddress": this.state.purToAddress,
            "etherWalletPassword": this.state.purEthvalpwd,
            "amount": this.state.purAmount
        }
        this.setState({ loading: true });
        const tokTransUrl = API_BASE_URL + "token/transfer/user";
        axios.post(tokTransUrl, payload)
            .then(response => {
                this.setState({ loading: false });
                if (response.status == 200) {
                    notify.show(response.data.message, "success");
                }
                else if (response.status == 206) {
                    notify.show(response.data.message, "error");
                }
                else if (response.data.message == 'Session Expired') {
                    this.props.history.push('/login');
                    notify.show(response.data.message, "error");
                }
            })
            .catch(function (error) {
                // console.log(error);
            });
        this.setState({ purToAddress: "", purAmount: "", purEthvalpwd: '' })
    }

    getEtherBalnce(value) {
        let token = {
            'sessionId': value.sessionId
        }
        this.setState({ sessionInfo: value });
        const apiBaseUrl = API_BASE_URL + "ether/balance";

        axios.post(apiBaseUrl, token)
            .then(response => {
                if (response.status === 200) {
                    this.setState({ ethBalance: response.data.etherBalanceInfo });
                } else if (response.data.message === 'Session expired!') {
                    sessionStorage.removeItem('userData');
                    this.props.history.push('/login');
                    let myColor = { background: 'red', text: '#FFFFFF' };
                    notify.show(response.data.message, 'custom', 5000, myColor);
                }
            })
    }
    getTokenBalnce(value) {
        let token = {
            'sessionId': value
        }
        const apiBaseUrl = API_BASE_URL + "token/balance";
        axios.post(apiBaseUrl, token)
            .then(response => {
                if (response.status === 200) {
                    this.setState({ tokenBalance: response.data.tokenBalance });
                } else if (response.data.message === 'Session expired!') {
                    sessionStorage.removeItem('loginInfo');
                    this.props.history.push('/login');
                    let myColor = { background: 'red', text: '#FFFFFF' };
                    notify.show(response.data.message, 'custom', 5000, myColor);
                }
            })
    }

    logOut() {
        let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
        let payload = {
            'sessionId': sessionInfo.sessionId
        }
        this.setState({ loading: true });
        const logoutUrl = API_BASE_URL + "logout";
        axios.post(logoutUrl, payload)
            .then(response => {
                this.setState({ loading: false });
                if (response.status == 200) {
                    sessionStorage.removeItem('loginInfo');
                    this.props.history.push('/login');
                    notify.show(response.data.message, "success");
                } else if (response.data.message == 'Session Expired') {
                    this.setState({ loading: false });
                    sessionStorage.removeItem('loginInfo');
                    sessionStorage.removeItem('kycInfo');
                    this.props.history.push('/login');
                    notify.show(response.data.message, "error");
                }
                else if (response.status == 206) {
                    notify.show(response.data.message, "error");
                }
            })
            .catch(function (error) {
                console.log(error);
            });
    }
    handleChange(e) {
        const value = e.target.value;
        const name = e.target.name;
        this.setState({ [name]: value },
            () => { this.validateField(name, value) });
    }
    copyClip() {
        this.setState({ copied: true });

    }
    validateField(fieldName, value) {
        let fieldValidationErrors = this.state.errors;
        let confirmPasswordValid = this.state.confirmPasswordValid;
        let passwordValid = this.state.passwordValid;
        let emailIdValid = this.state.emailIdValid;


        switch (fieldName) {
            case 'emailId':
                emailIdValid = value.match(/^([\w.%+-]+)@([\w-]+\.)+([\w]{2,})$/i);
                fieldValidationErrors.emailId = emailIdValid ? '' : ' Please enter valid email id';
                break
            default:
                break;
        }

        if (fieldName === 'password') {
            fieldValidationErrors.password = value.length > 8 ? '' : 'Must contain minimum 8 character';
            if (value != this.state.password) {
                fieldValidationErrors.confirmPassword = 'Password does not match';
            } else if (this.state.confirmPassword != "") {

                if (value != this.state.confirmPassword) {
                    fieldValidationErrors.confirmPassword = 'Password does not match';
                } else {
                    fieldValidationErrors.confirmPassword = '';
                }
            }
        } else if (fieldName === 'confirmPassword') {
            if (value != this.state.password) {
                fieldValidationErrors.confirmPassword = 'Password does not match';
            } else {
                fieldValidationErrors.confirmPassword = '';

            }
        }
        this.setState({
            errors: fieldValidationErrors,
            confirmPasswordValid: confirmPasswordValid,
            passwordValid: passwordValid,
            emailIdValid: emailIdValid
        }, this.validateForm);
    }
    validateForm() {
        this.setState({ formValid: this.state.emailIdValid });
    }
    toggleResetPwd() {
        let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
        const payload = {
            oldPassword: this.state.oldPassword,
            password: this.state.password,
            confirmPassword: this.state.confirmPassword,
            securityKey: this.state.secretPin,
            sessionId: sessionInfo.sessionId
        }
        this.setState({ loading: true });
        const resetPwdUrl = API_BASE_URL + "reset/password";
        axios.post(resetPwdUrl, payload)
            .then(response => {
                this.setState({ loading: false });
                if (response.status == 200) {
                    this.props.history.push('/login');
                    notify.show(response.data.message, "success");
                } else if (response.status == 206) {
                    notify.show(response.data.message, "error");
                }
                else if (response.data.message == 'Session Expired') {
                    this.props.history.push('/login');
                    notify.show(response.data.message, "error");
                }
            })
            .catch(function (error) {
                console.log(error);
            });
        this.setState({ oldPassword: '', password: '', confirmPassword: '', secretPin: '' })
    }
    toggleValidate(event) {
        event.preventDefault();
        let errors = {}
        if (event.target.name == 'ethvalpwd') {
            if (event.target.value.length < 8) {
                errors.ethvalpwd = "Please enter valid ether wallet password";
            }
        }
        if (event.target.name == 'tokens') {
            if (!validator.isNumeric(event.target.value)) {
                errors.tokens = "Please enter valid amount";
            }

        }
        this.setState({ [event.target.name]: event.target.value });
        this.setState({ errors });

    }
    render() {
        let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
        return (
            <div>
                {this.state.loading && <div className='loaderBg'>
                    <div className='loaderimg'>
                        <ScaleLoader
                            size={180}
                            color={'#fff'}
                            loading={this.state.loading}
                        />
                    </div>
                </div>}
                <section id="container">
                    <Notifications />
                    <header className="header fixed-top clearfix">
                        <div className="brand">
                            <a className="logo">
                                <img src="src/public/image/aleef-token.png" /> </a>
                            <ToggleMenu />
                        </div>
                        <div className="top-nav clearfix">
                            <ul className="nav pull-right top-menu">
                                <li className="dropdown">
                                    <a data-toggle="dropdown" className="dropdown-toggle" href="#">
                                        <span>{this.state.sessionInfo.userName}</span>
                                        <img alt="" src="src/public/image/user.png" /> </a>
                                    <ul className="dropdown-menu extended logout">
                                        <li>
                                            <NavLink to='/kycdetails' >
                                                <i className="fa fa-user"></i> My Profile</NavLink>
                                        </li>
                                        <li>
                                            <a data-toggle="modal" data-target="#resetpwd">
                                                <i className="fa fa-cog"></i> Reset Password</a>
                                        </li>
                                        <li>
                                            <a onClick={this.logOut}>
                                                <i className="fa fa-sign-out"></i> Log Out</a>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                    </header>
                    <aside>
                        <div id="sidebar" className="nav-collapse">
                            <div className="leftside-navigation">
                                <ul className="sidebar-menu" id="nav-accordion">
                                    <li className="nav-profile logo-nav"></li>

                                    <li>
                                        <NavLink to={'/userdashboard'}>
                                            <img src="src/public/image/dashboard.png" />
                                            <span className="m_left">Dashboard</span>
                                        </NavLink>
                                    </li>
                                    <li>
                                        <NavLink to={'/usertransaction'}>
                                            <img src="src/public/image/transaction.png" />
                                            <span className="m_left">My Transaction</span>
                                        </NavLink>
                                    </li>
                                    <li>
                                        <NavLink to={'/refferdlist'}>
                                            <img src="src/public/image/transaction.png" />
                                            <span className="m_left">My Referrals</span>
                                        </NavLink>
                                    </li>
                                    <li>
                                        <NavLink to={'/kycdetails'}>
                                            <img src="src/public/image/transaction.png" />
                                            <span className="m_left">My Profile</span>
                                        </NavLink>
                                    </li>
                                    <li>
                                        <NavLink to={'/purchaseList'}>
                                            <img src="src/public/image/userpurchase.png" />
                                            <span className="m_left">Purchase List</span>
                                        </NavLink>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </aside>
                    <section id="main-content">
                        <div className="wrapper">
                            <div className="col-md-12 col-xs-12 col-sm-12">
                                <div className="usdether">
                                    <p className="blink">1 USD = {1 / this.state.ethValue} ETH</p>
                                    <p className="usdcoinbase"> [Ref: <a href="https://www.coinbase.com/" target="_blank">Coinbase.com</a>]</p>
                                </div>
                            </div>
                            <div className="dashboard-title">
                                <h1>User
                        <span>Dashboard</span>
                                </h1>
                                <button type="button" data-toggle="modal" data-target="#referralLinkpopup" className="referrallink">Referral</button>
                            </div>
                            <div className="user-board-fullwrap">
                                <div className="col-md-12 col-xs-12 col-sm-12">
                                    <div className="aleef-wallet-id">
                                        <h1>Wallet Address :</h1>
                                        <p>{this.state.sessionInfo.etherWalletAddress}</p>

                                    </div>
                                </div>
                                <div className="balance-list-box">
                                    <div className="col-md-4 col-xs-12 col-sm-4">
                                        <div className="list-box-3">
                                            <h2>Ether Balance</h2>
                                            <img src="src/public/image/etherum.png" />

                                            <h4>{this.state.ethBalance.etherBalance}</h4>
                                        </div>
                                    </div>
                                    <div className="col-md-4 col-xs-12 col-sm-4">
                                        <div className="list-box-3">
                                            <h2>Token Balance<sup>*</sup></h2>
                                            <img src="src/public/image/user-bit-coin.png" />
                                            <h4>{this.state.tokenBalance.tokenAmount}</h4>
                                            <div className="inclusive">
                                                <p><sup className="inclusivestart">*</sup>Inclusive of Referral Points</p>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-4 col-xs-12 col-sm-4" onClick={() => {
                                        this.setState({ isRefBouns: true })
                                        this.getRefferalBouns();
                                    }}>
                                        <div className="list-box-3">
                                            <h2>Referral points</h2>
                                            <img src="src/public/image/token-bal.png" />
                                            <h4>{this.state.refBouns.referralTokens}</h4>
                                        </div>
                                    </div>
                                </div>
                                <div className="transaction-wrap">
                                    <div className="row">
                                        <div className="col-md-12 col-xs-12 col-sm-12">
                                            <div className="referlink">
                                                <p>Don't have Ethereum.. Need to Buy? You can use any of these Third-party Ether Seller at your own risk<br />
                                                    <a href="https://shapeshift.io" target="_blank">  ShapeShift</a>&nbsp;&nbsp;&nbsp;
                                                    <a href="https://www.myetherwallet.com" target="_blank">   MyEtherwallet  </a>&nbsp;&nbsp;&nbsp;
                                                    <a href="https://www.coinbase.com/" target="_blank">   Coinbase.com   </a></p>
                                                <p className="discaimer"><span>Disclaimer :</span>You can buy the Ether at your own risk, Aleef will not hold any kind of responsibility if any loss in transaction.</p>
                                            </div>
                                        </div>
                                    </div>

                                    <div className="row">
                                        <div className="col-md-6 col-xs-12 col-sm-6">
                                            <div className="purchase-coin">
                                                <div className="purchse-title">
                                                    <h1>Purchase Coin</h1>
                                                </div>
                                                <div className="purchase-body">
                                                    <div className="purchase-logo">
                                                        <img src="src/public/image/aleef-token.png" />
                                                    </div>
                                                    <div className="ethcountvalue">
                                                        {this.state.tokens ? <p>{this.state.tokens}AC = {this.state.tokens * this.state.saleOffer / this.state.ethValue}ETH</p> : null}
                                                    </div>
                                                    <div className="purchase-div">
                                                        <input type="number" className='form-control' placeholder='Enter no of coins' name='tokens' value={this.state.tokens || ''} onChange={this.toggleValidate} min="1" /> <br />
                                                        <div style={{ color: 'red' }}>{this.state.errors.tokens}</div>
                                                        <input type="password" className='form-control' placeholder='Ether wallet password' name='ethvalpwd' value={this.state.ethvalpwd || ''} onChange={this.toggleValidate} />
                                                        <div style={{ color: 'red' }}>{this.state.errors.ethvalpwd}</div>
                                                    </div>
                                                    <div className="purchase-bttn">
                                                        <button type="button" className="btn btn-purchase-coin" onClick={this.togglePurchase}>Purchase Coin</button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-md-6 col-xs-12 col-sm-6">
                                            <div className="purchase-coin">
                                                <div className="purchse-title dark-b">
                                                    <h1>Recent Transaction</h1>
                                                </div>
                                                {this.state.recTrans.length > 0 ? <div className="purchase-body user-body">
                                                    {
                                                        this.state.recTrans.map((items, key) =>

                                                            <div className="user-profile" key={key}>
                                                                <div className="user-avatar">
                                                                    <img src="src/public/image/user-1.png" />
                                                                </div>
                                                                <div className="user-details">
                                                                    <h5>{items.fromAddress}</h5>
                                                                    <p><span className="sentto">{items.typeOfStatus} {items.transactionAmount} {items.transactionMode} </span> to {items.toAddress}</p>
                                                                    <span>{items.transactionDate}</span>
                                                                </div>
                                                            </div>
                                                        )
                                                    }
                                                </div> : <div> No Recent Transaction Available </div>}
                                            </div>
                                        </div>
                                        <div className="coin-transfer">
                                            <div className="container-fluid">
                                                <div className="row">
                                                    <div className="col-md-12 col-sm-12 col-xs-12 coin-transfer-padd">
                                                        <div className="transaction-title">
                                                            <h1>Coin<span>Transfer</span>
                                                            </h1>
                                                        </div>
                                                        <div className="coin-transfer-wrap">
                                                            <form className="adjust_padding">
                                                                <div className="form-group">
                                                                    <label htmlFor="wallet"> Wallet Address</label>
                                                                    <input type="text" className="form-control" placeholder="Enter wallet address" name="purToAddress" value={this.state.purToAddress || ''} onChange={this.cointransChange} />
                                                                    <div style={{ color: 'red' }}>{this.state.errors.purToAddress}</div>
                                                                </div>
                                                                <div className="form-group">
                                                                    <label htmlFor="wallet"> Number of coins</label>
                                                                    <input type="number" min="1" className="form-control" placeholder="Enter no of coins" name="purAmount" value={this.state.purAmount || ''} onChange={this.cointransChange} />
                                                                    <div style={{ color: 'red' }}>{this.state.errors.purAmount}</div>
                                                                </div>
                                                                <div className="form-group">
                                                                    <label htmlFor="wallet"> Ether wallet password </label>
                                                                    <input type="password" className="form-control" placeholder="Ether wallet password" name="purEthvalpwd" value={this.state.purEthvalpwd || ''} onChange={this.cointransChange} />
                                                                    <div style={{ color: 'red' }}>{this.state.errors.purEthvalpwd}</div>
                                                                </div>
                                                                <div className="form-group send-btn-bttm">
                                                                    <button style={{ color: '#fff' }} type="button" className="btn btn-send" onClick={this.toggleTransToken} disabled={this.state.purToAddress == '' || this.state.purAmount == '' || this.state.purEthvalpwd == ''}>Send</button>
                                                                </div>
                                                            </form>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </section>
                </section>
                {/* reset password */}
                <div className="reset-password-wrap">
                    <div className="modal fade" id="resetpwd" role="dialog">
                        <div className="modal-dialog">
                            <div className="modal-content">
                                <div className="modal-header">
                                    <button type="button" className="close" data-dismiss="modal">&times;</button>
                                    <h4 className="modal-title">Reset Password</h4>
                                </div>
                                <div className="modal-body">
                                    <div className="reset-password">
                                        <form action="" method="post">
                                            <div className="form-group">
                                                <input type="password" className="form-control" placeholder="Old-Password" name="oldPassword" value={this.state.oldPassword || ''} onChange={this.handleChange1} />
                                                <div style={{ color: 'red' }}>{this.state.errors.oldPassword}</div>
                                            </div>
                                            <div className="form-group">
                                                <input type="password" className="form-control" placeholder="New-Password" name="password" value={this.state.password || ''} onChange={this.handleChange1} />
                                                <div style={{ color: 'red' }}>{this.state.errors.password}</div>
                                            </div>
                                            <div className="form-group">
                                                <input type="password" className="form-control" placeholder="Confirm-password" name="confirmPassword" value={this.state.confirmPassword || ''} onChange={this.handleChange1} />
                                                <div style={{ color: 'red' }}>{this.state.errors.confirmPassword}</div>
                                            </div>
                                            <div className="form-group">
                                                <input type="password" className="form-control" placeholder="Enter Secret Pin" name="secretPin" value={this.state.secretPin || ''} onChange={this.handleChange} />
                                            </div>
                                        </form>
                                    </div>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-resent" data-dismiss="modal" onClick={this.toggleResetPwd} disabled={this.state.oldPassword == "" || this.state.password == "" || this.state.confirmPassword == ""} >Submit</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                {/* referral link popup */}
                <div className="reset-password-wrap">
                    <div className="modal fade" id="referralLinkpopup" role="dialog">
                        <div className="modal-dialog">
                            <div className="modal-content modalRef">
                                <div className="modal-header">
                                    <button type="button" className="close" data-dismiss="modal">&times;</button>
                                    <h4 className="modal-title">Referral Link</h4>
                                </div>
                                <div className="modal-body">
                                    <div className="reset-password">
                                        <div className="form-group">
                                            <input type="text" className="form-control referralInput" name="refValue" value={this.state.sessionInfo.referralLink || ''} readOnly="readOnly" />
                                            <CopyToClipboard text={this.state.sessionInfo.referralLink}>
                                                <button type="button" className="referralCopy" onClick={this.copyClip}>Copy</button>
                                            </CopyToClipboard>
                                            {this.state.copied ? <span style={{ color: 'red' }}>Copied.</span> : null}
                                        </div>
                                        <h4 className="referralh4">Or</h4>
                                        <form action="" method="post">

                                            <div className="form-group">

                                                <input type="text" placeholder="Email id" name='emailId' value={this.state.emailId || ''} onChange={this.handleChange} />
                                                <div style={{ color: 'red' }}>{this.state.errors.emailId}</div>
                                            </div>
                                            <div className="referralSubBtn">
                                                <button type="button" className="btn btn-resent" onClick={this.refLinkclick} disabled={!this.state.formValid} style={{ marginBottom: "10px" }} data-dismiss="modal">Submit</button>
                                            </div>
                                        </form>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>
                {this.state.isRefBouns && <div class="referral-bonus">
                    <div class="referral-bonus-div">
                        <h1>Referral Bonus</h1>
                        <div class="burn-body-cont">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>User Name</th>
                                        <th>{this.state.refBouns.userName}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>Level 1</td>
                                        <td class="text-right">{this.state.refBouns.referralLevel1Tokens}</td>
                                    </tr>
                                    <tr>
                                        <td>Level 2</td>
                                        <td class="text-right">{this.state.refBouns.referralLevel2Tokens}</td>
                                    </tr>
                                    <tr>
                                        <td>Level 3</td>
                                        <td class="text-right">{this.state.refBouns.referralLevel3Tokens}</td>
                                    </tr>
                                    <tr>
                                        <td>Level 4</td>
                                        <td class="text-right">{this.state.refBouns.referralLevel4Tokens}</td>
                                    </tr>
                                    <tr>
                                        <td class="texttotla">Total</td>
                                        <td class="text-right">{this.state.refBouns.referralTokens}</td>
                                    </tr>
                                </tbody>
                            </table>
                            <button type="button" class="btnclose" onClick={() => { this.setState({ isRefBouns: false }) }}>Close</button>
                        </div>
                    </div>
                </div>}
            </div>
        )
    }
}
export default UserDashboard;