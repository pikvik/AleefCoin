import React from 'react';
import { NavLink } from 'react-router-dom';
import Pagination from 'react-js-pagination';
import axios from 'axios';
import { ScaleLoader } from 'react-spinners';
import Notifications, { notify } from 'react-notify-toast';
import { API_BASE_URL } from '../Common/apiUrl';
import AleefBoard from '../Common/aleefBoard';
import ToggleMenu from '../Common/togglemenu';

class AdminTransaction extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            allTransHistory: [],
            sendTransHistory: [],
            recieveTransHistory: [],
            loading: false,
            page: 1,
            sendPage: 1,
            recievePage: 1,
            perPage: 10,
            searchFilter: '',
            errors: { oldPassword: '', password: '', confirmPassword: '' },
            oldPasswordValid: false,
            passwordValid: false,
            confirmPasswordValid: false,
            loading: false,
            sessionInfo: [],
            secretPin: '',
            isfilter: true
        }
        if (sessionStorage.getItem('loginInfo') == null) {
            props.history.push('/login');
        }
        this.getAllTransaction = this.getAllTransaction.bind(this);
        this.getSendTransaction = this.getSendTransaction.bind(this);
        this.getRecievedTransaction = this.getRecievedTransaction.bind(this);
        this.handlePageChange = this.handlePageChange.bind(this);
        this.sendPagechange = this.sendPagechange.bind(this);
        this.getStatus = this.getStatus.bind(this);
        this.receivePagechange = this.receivePagechange.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.filterFunc = this.filterFunc.bind(this);
        this.logOut = this.logOut.bind(this);
        this.toggleResetPwd = this.toggleResetPwd.bind(this);
    }
    componentDidMount() {
        if (sessionStorage.getItem('loginInfo') != null) {
            let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
            this.getAllTransaction(sessionInfo);
            this.getSendTransaction();
            this.getRecievedTransaction();
        }
    }
    handleChange(e) {
        const value = e.target.value;
        const name = e.target.name;
        this.setState({ [name]: value },
            () => { this.validateField(name, value) });
    }
    validateField(fieldName, value) {
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
    filterFunc(e) {
        if (sessionStorage.getItem('loginInfo') != null) {
            let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
            if (this.state.searchFilter.length <= 3) {
                let payload = {
                    'sessionId': sessionInfo.loginInfo.sessionId,
                    'transactionMode': this.state.searchFilter
                }
                const apiBaseUrl = API_BASE_URL + "token/transaction/filter/mode";
                axios.post(apiBaseUrl, payload)
                    .then(Response => {
                        if (Response.status == '200') {
                            this.setState({ allTransHistory: Response.data.purchaseListInfo });
                        } else {
                            if (response.data.message == 'Session Expired') {
                                this.props.history.push('/login');
                            }
                        }
                    });
            }
            else {
                let payload = {
                    'sessionId': sessionInfo.loginInfo.sessionId,
                    'etherWalletAddress': this.state.searchFilter
                }
                const apiBaseUrl = API_BASE_URL + "token/transaction/filter/address";
                axios.post(apiBaseUrl, payload)
                    .then(Response => {
                        if (Response.status == '200') {
                            this.setState({ allTransHistory: Response.data.purchaseListInfo });

                        } else {
                            if (response.data.message == 'Session Expired') {
                                this.props.history.push('/login');
                            }
                        }
                    });
            }
        }
    }
    getStatus(value) {
        switch (value) {
            case 1:
                return 'Success';
            case 2:
                return 'Failed';
            case 0:
                return 'Pending';
        }
    }
    getAllTransaction(info) {
        let payload = {
            'sessionId': info.loginInfo.sessionId,
            'etherWalletAddress': info.loginInfo.etherWalletAddress,
            'transactionType': '0'
        }
        this.setState({ sessionInfo: info })
        this.setState({ loading: true });
        const apiBaseUrl = API_BASE_URL + "token/transactionHistory";
        axios.post(apiBaseUrl, payload)
            .then(Response => {
                this.setState({ loading: false });
                if (Response.status == '200') {
                    this.setState({ allTransHistory: Response.data.transactionHistoryInfo })
                } else {
                    if (response.data.message == 'Session Expired') {
                        this.setState({ loading: false });
                        this.props.history.push('/login');
                    }
                }
            })
            .catch(function (error) {
            });
    }
    getSendTransaction() {
        let info = JSON.parse(sessionStorage.getItem('loginInfo'));
        let payload = {
            'sessionId': info.loginInfo.sessionId,
            'etherWalletAddress': info.loginInfo.etherWalletAddress,
            'transactionType': '1'
        }
        this.setState({ loading: true });

        const apiBaseUrl = API_BASE_URL + "token/transactionHistory";
        axios.post(apiBaseUrl, payload)
            .then(Response => {
                this.setState({ loading: false });

                if (Response.status == '200') {

                    this.setState({ sendTransHistory: Response.data.transactionHistoryInfo })
                } else {
                    if (response.data.message == 'Session Expired') {
                        this.setState({ loading: false });
                        this.props.history.push('/login');
                    }
                }
            })
            .catch(function (error) {
            });
    }
    getRecievedTransaction() {
        let info = JSON.parse(sessionStorage.getItem('loginInfo'));
        let payload = {
            'sessionId': info.loginInfo.sessionId,
            'etherWalletAddress': info.loginInfo.etherWalletAddress,
            'transactionType': '2'
        }
        const apiBaseUrl = API_BASE_URL + "token/transactionHistory";
        axios.post(apiBaseUrl, payload)
            .then(Response => {
                if (Response.status == '200') {
                    let recTransHis = [];
                    if (Response.data.transactionHistoryInfo.length > 0) {
                        Response.data.transactionHistoryInfo.map((i, key) => {
                            if ((i.transactionStatus !== 0)) {
                                recTransHis.push(i);
                            }
                        })
                    }
                    this.setState({ recieveTransHistory: recTransHis })
                } else {
                    if (response.data.message == 'Session Expired') {
                        history.push('/login');
                    }
                }
            })
            .catch(function (error) {
            });
    }
    handlePageChange(page) {
        this.setState({ page })
    }
    sendPagechange(sendPage) {
        this.setState({ sendPage })
    }
    receivePagechange(recievePage) {
        this.setState({ recievePage })
    }
    logOut() {
        let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
        let payload = {
            'sessionId': sessionInfo.loginInfo.sessionId
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
    toggleResetPwd() {
        let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
        const payload = {
            oldPassword: this.state.oldPassword,
            password: this.state.password,
            confirmPassword: this.state.confirmPassword,
            sessionId: sessionInfo.loginInfo.sessionId
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
        this.setState({ oldPassword: '', password: '', confirmPassword: '' })
    }
    render() {
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
                    {/* <!--Topbar--> */}
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
                                    <a data-toggle="dropdown" className="dropdown-toggle" >
                                        <span>{this.state.sessionInfo.userName}</span>
                                        <img alt="" src="src/public/image/user.png" /> </a>
                                    <ul className="dropdown-menu extended logout">
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
                    {/* <!--Sidebar--> */}
                    <aside>
                        <div id="sidebar" className="nav-collapse">
                            <div className="leftside-navigation">
                                <ul className="sidebar-menu" id="nav-accordion">
                                    <li className="nav-profile logo-nav"></li>
                                    <li>
                                        <NavLink to={'/admindashboard'}>
                                            <img src="src/public/image/dashboard.png" />
                                            <span className="m_left">Dashboard</span>
                                        </NavLink>
                                    </li>
                                    <li>
                                        <NavLink to={'/admintransaction'}>
                                            <img src="src/public/image/transaction.png" />
                                            <span className="m_left">My Transaction</span>
                                        </NavLink>
                                    </li>
                                    <li>
                                        <NavLink to={'/managekyc'}>
                                            <img src="src/public/image/transaction.png" />
                                            <span className="m_left">Manage KYC</span>
                                        </NavLink>
                                    </li>
                                    <li>
                                        <NavLink to={'/userList'}>
                                            <img src="src/public/image/userlist.png" />
                                            <span className="m_left">User List</span>
                                        </NavLink>
                                    </li>
                                    <li>
                                        <NavLink to={'/userPurchaseList'}>
                                            <img src="src/public/image/userpurchase.png" />
                                            <span className="m_left">User Purchase List</span>
                                        </NavLink>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </aside>
                    <section id="main-content">
                        <section className="wrapper">
                            <div className="dashboard-title title-inline">
                                <h1>My
                        <span>Transaction</span>
                                </h1>
                                {this.state.isfilter &&
                                    <div className="searchbox-select">
                                        <form>
                                            <select>
                                                <option value="all">--Select Filter--</option>
                                                <option value="dummy">Transaction Mode</option>
                                                <option value="dummy">Ether Wallet Address</option>
                                            </select>
                                            <div className="searchinput">
                                                <input type="text" className="form-control" placeholder="Search here" name="searchFilter" value={this.state.searchFilter || ''} onChange={this.handleChange} />
                                                <i className="fa fa-search" style={{ cursor: 'pointer' }} aria-hidden="true" onClick={this.filterFunc}></i>
                                            </div>
                                        </form>
                                    </div>}
                            </div>
                            <section className="admin-content">
                                <div className="data-menchorcoin">
                                    <div className="row">
                                        <div className="col-md-12">

                                            {/* <!-- Nav tabs --> */}
                                            <div className="card">
                                                <ul className="nav nav-tabs" role="tablist">
                                                    <li role="presentation" className="active">
                                                        <a href="#all" aria-controls="all" role="tab" data-toggle="tab" onClick={() => { this.setState({ isfilter: true }) }}>All</a>
                                                    </li>
                                                    <li role="presentation">
                                                        <a href="#send" aria-controls="send" role="tab" data-toggle="tab" onClick={() => { this.setState({ isfilter: false }) }}>Sent</a>
                                                    </li>
                                                    <li role="presentation">
                                                        <a href="#received" aria-controls="received" role="tab" data-toggle="tab" onClick={() => { this.setState({ isfilter: false }) }}>Received</a>
                                                    </li>

                                                </ul>

                                                {/* <!-- Tab panes --> */}
                                                <div className="tab-content">
                                                    <div role="tabpanel" className="tab-pane active" id="all">
                                                        <div className="table-responsive">


                                                            {this.state.allTransHistory.length > 0
                                                                ? <table className="table">
                                                                    <thead>
                                                                        <tr><th>From Address</th>
                                                                            <th>To Address</th>
                                                                            <th>Amount</th>
                                                                            <th> Mode </th>
                                                                            <th>Date &amp; Time</th>
                                                                            <th>Status</th>
                                                                        </tr></thead>
                                                                    <tbody>
                                                                        {
                                                                            this.state.allTransHistory.map((item, key) =>
                                                                                this.state.page * this.state.perPage > key &&
                                                                                (this.state.page - 1) * this.state.perPage <= key &&
                                                                                <tr key={key}>
                                                                                    <td >{item.fromAddress}</td>
                                                                                    <td>{item.toAddress}</td>
                                                                                    <td>{item.transactionAmount}</td>
                                                                                    <td>{item.transactionMode}</td>
                                                                                    <td>{item.transactionDate}</td>
                                                                                    <td>{item.transferStatus}</td>

                                                                                </tr>
                                                                            )
                                                                        }
                                                                    </tbody>
                                                                </table >
                                                                : <span>No Transactions Yet</span>
                                                            }



                                                            <div>
                                                                {this.state.allTransHistory.length > 0 &&
                                                                    <Pagination
                                                                        activePage={this.state.page}
                                                                        itemsCountPerPage={this.state.perPage}
                                                                        totalItemsCount={this.state.allTransHistory.length}
                                                                        pageRangeDisplayed={5}
                                                                        onChange={this.handlePageChange} />
                                                                }
                                                            </div>

                                                        </div>
                                                    </div>
                                                    <div role="tabpanel" className="tab-pane" id="send">
                                                        <div className="table-responsive">
                                                            {this.state.sendTransHistory.length > 0
                                                                ? <table className="table">
                                                                    <thead>
                                                                        <tr><th>From Address</th>
                                                                            <th>To Address</th>
                                                                            <th>Amount</th>
                                                                            <th> Mode </th>
                                                                            <th>Date &amp; Time</th>
                                                                            <th>Status</th>
                                                                        </tr></thead>
                                                                    <tbody>
                                                                        {
                                                                            this.state.sendTransHistory.map((item, key) =>
                                                                                this.state.sendPage * this.state.perPage > key &&
                                                                                (this.state.sendPage - 1) * this.state.perPage <= key &&
                                                                                <tr key={key}>
                                                                                    <td >{item.fromAddress}</td>
                                                                                    <td>{item.toAddress}</td>
                                                                                    <td>{item.transactionAmount}</td>
                                                                                    <td>{item.transactionMode}</td>
                                                                                    <td>{item.transactionDate}</td>
                                                                                    <td>{item.transferStatus}</td>
                                                                                </tr>
                                                                            )
                                                                        }
                                                                    </tbody>
                                                                </table >
                                                                : <span>No Transactions Yet</span>
                                                            }

                                                            <div>
                                                                {this.state.sendTransHistory.length > 0 &&
                                                                    <Pagination
                                                                        activePage={this.state.sendPage}
                                                                        itemsCountPerPage={this.state.perPage}
                                                                        totalItemsCount={this.state.sendTransHistory.length}
                                                                        pageRangeDisplayed={5}
                                                                        onChange={this.sendPagechange} />
                                                                }
                                                            </div>
                                                        </div>
                                                    </div>


                                                    <div role="tabpanel" className="tab-pane" id="received">
                                                        <div className="table-responsive">
                                                            {this.state.recieveTransHistory.length > 0
                                                                ? <table className="table">
                                                                    <thead>
                                                                        <tr><th>From Address</th>
                                                                            <th>To Address</th>
                                                                            <th>Amount</th>
                                                                            <th> Mode </th>
                                                                            <th>Date &amp; Time</th>
                                                                            <th>Status</th>
                                                                        </tr></thead>
                                                                    <tbody>
                                                                        {
                                                                            this.state.recieveTransHistory.map((item, key) =>
                                                                                this.state.recievePage * this.state.perPage > key &&
                                                                                (this.state.recievePage - 1) * this.state.perPage <= key &&
                                                                                <tr key={key}>
                                                                                    <td >{item.fromAddress}</td>
                                                                                    <td>{item.toAddress}</td>
                                                                                    <td>{item.transactionAmount}</td>
                                                                                    <td>{item.transactionMode}</td>
                                                                                    <td>{item.transactionDate}</td>
                                                                                    <td>{item.transferStatus}</td>
                                                                                </tr>
                                                                            )
                                                                        }
                                                                    </tbody>
                                                                </table >
                                                                : <span>No Transactions Yet</span>
                                                            }

                                                            <div>
                                                                {this.state.recieveTransHistory.length > 0 &&
                                                                    <Pagination
                                                                        activePage={this.state.recievePage}
                                                                        itemsCountPerPage={this.state.perPage}
                                                                        totalItemsCount={this.state.recieveTransHistory.length}
                                                                        pageRangeDisplayed={5}
                                                                        onChange={this.receivePagechange} />
                                                                }
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
                                                <input type="password" className="form-control" placeholder="Old-Password" name="oldPassword" value={this.state.oldPassword || ''} onChange={this.handleChange} />
                                                <div style={{ color: 'red' }}>{this.state.errors.oldPassword}</div>
                                            </div>
                                            <div className="form-group">
                                                <input type="password" className="form-control" placeholder="New-Password" name="password" value={this.state.password || ''} onChange={this.handleChange} />
                                                <div style={{ color: 'red' }}>{this.state.errors.password}</div>
                                            </div>
                                            <div className="form-group">
                                                <input type="password" className="form-control" placeholder="Confirm-password" name="confirmPassword" value={this.state.confirmPassword || ''} onChange={this.handleChange} />
                                                <div style={{ color: 'red' }}>{this.state.errors.confirmPassword}</div>
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
            </div>
        )
    }
}

export default AdminTransaction;