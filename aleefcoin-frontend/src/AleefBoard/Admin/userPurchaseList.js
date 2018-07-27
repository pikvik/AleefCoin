import React from 'react';
import { NavLink } from 'react-router-dom';
import axios from 'axios';
import { API_BASE_URL } from '../Common/apiUrl';
import Pagination from 'react-js-pagination';
import Notifications, { notify } from 'react-notify-toast';
import { ScaleLoader } from 'react-spinners';
import ToggleMenu from '../Common/togglemenu';

class userPurchaseList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            purchaseList: [],
            page: 1,
            sendPage: 1,
            recievePage: 1,
            perPage: 10,
            loading: false,
            errors: { oldPassword: '', password: '', confirmPassword: '' },
            oldPasswordValid: false,
            passwordValid: false,
            confirmPasswordValid: false,
            sessionInfo: [],
            secretPin: '',
            searchFilter: ''
        }
        if (sessionStorage.getItem('loginInfo') == null) {
            props.history.push('/login');
        }
        this.getPurchaseInfo = this.getPurchaseInfo.bind(this);
        this.logOut = this.logOut.bind(this);
        this.handlePageChange = this.handlePageChange.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.getStatus = this.getStatus.bind(this);
        this.getStatus = this.getStatus.bind(this);
        this.toggleResetPwd = this.toggleResetPwd.bind(this);
        this.filterFunc = this.filterFunc.bind(this);
    }
    componentDidMount() {
        if (sessionStorage.getItem('loginInfo') != null) {
            let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
            this.getPurchaseInfo(sessionInfo);
        }
    }
    getStatus(value) {
        switch (value) {
            case 1:
                return 'Success';
            case 0:
                return 'Pending';
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
    getPurchaseInfo(info) {
        let payload = {
            'sessionId': info.loginInfo.sessionId,
            'etherWalletAddress': info.loginInfo.etherWalletAddress
        }
        this.setState({ sessionInfo: info })
        const apiBaseUrl = API_BASE_URL + "token/purchase/list";
        axios.post(apiBaseUrl, payload)
            .then(Response => {
                if (Response.status == '200') {
                    this.setState({ purchaseList: Response.data.purchaseListInfo })
                }
                else {
                    if (response.data.message == 'Session Expired') {
                        this.props.history.push('/login');
                    }
                }

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
    handlePageChange(page) {
        this.setState({ page })
    }
    filterFunc(e) {
        if (sessionStorage.getItem('loginInfo') != null) {
            let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
            if (this.state.searchFilter.length > 25) {
                let payload = {
                    'sessionId': sessionInfo.loginInfo.sessionId,
                    'etherWalletAddress': this.state.searchFilter
                }
                this.setState({ loading: true })
                const apiBaseUrl = API_BASE_URL + "token/purchase/list/filter";
                axios.post(apiBaseUrl, payload)
                    .then(Response => {
                        this.setState({ loading: false })
                        if (Response.status == '200') {
                            this.setState({ purchaseList: Response.data.purchaseListInfo });
                        } else {
                            if (response.data.message == 'Session Expired') {
                                this.props.history.push('/login');
                                notify.show(Response.data.message, 'error');
                            }
                        }
                    });
            }
            else {
                let payload = {
                    'sessionId': sessionInfo.loginInfo.sessionId,
                    'userName': this.state.searchFilter
                }
                this.setState({ loading: true })
                const apiBaseUrl = API_BASE_URL + "token/purchase/list/filter";
                axios.post(apiBaseUrl, payload)
                    .then(Response => {
                        this.setState({ loading: false })
                        if (Response.status == '200') {
                            this.setState({ purchaseList: Response.data.purchaseListInfo });

                        } else {
                            if (response.data.message == 'Session Expired') {
                                this.props.history.push('/login');
                                notify.show(Response.data.message, 'error');
                            }
                        }
                    });
            }
        }
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
                                <h1>User
                        <span> Purchase List</span>
                                </h1>
                                <div className="searchbox-select">
                                    <form>
                                        <select>
                                            <option value="all">--Filter--</option>
                                            <option value="dummy">Ether Wallet Address</option>
                                            <option value="dummy">User Name</option>
                                        </select>
                                        <div className="searchinput">
                                            <input type="text" className="form-control" placeholder="Search here" name="searchFilter" value={this.state.searchFilter || ''} onChange={this.handleChange} />
                                            <i className="fa fa-search" style={{ cursor: 'pointer' }} aria-hidden="true" onClick={this.filterFunc}></i>
                                        </div>
                                    </form>
                                </div>
                            </div>
                            <section className="admin-content">
                                <div className="data-menchorcoin">
                                    <div className="row">
                                        <div className="col-md-12">
                                            <div role="tabpanel" className="tab-pane active" id="all">
                                                <div className="table-responsive">
                                                    <table className="table">
                                                        <thead>
                                                            <tr><th>User Name</th>
                                                                <th>Ether Wallet Address</th>
                                                                <th>Request Tokens</th>
                                                                <th>Free Tokens </th>
                                                                <th>Ether Amount</th>
                                                                <th>Date &amp; Time</th>
                                                                <th>Status</th>
                                                            </tr></thead>
                                                        <tbody>
                                                            {this.state.purchaseList.map((item, key) =>
                                                                this.state.page * this.state.perPage > key &&
                                                                (this.state.page - 1) * this.state.perPage <= key &&
                                                                <tr key={key}>
                                                                    <td >{item.userName}</td>
                                                                    <td>{item.etherWalletAddress}</td>
                                                                    <td>{item.requestTokens}</td>
                                                                    <td>{item.freeTokens}</td>
                                                                    <td>{item.etherAmount}</td>
                                                                    <td>{item.purchasedDate}</td>
                                                                    <td>{this.getStatus(item.purchaseStatus)}</td>
                                                                </tr>
                                                            )}

                                                        </tbody>
                                                    </table >



                                                    <div>
                                                        {this.state.purchaseList.length > 0 &&
                                                            <Pagination
                                                                activePage={this.state.page}
                                                                itemsCountPerPage={this.state.perPage}
                                                                totalItemsCount={this.state.purchaseList.length}
                                                                pageRangeDisplayed={5}
                                                                onChange={this.handlePageChange} />
                                                        }
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
export default userPurchaseList;