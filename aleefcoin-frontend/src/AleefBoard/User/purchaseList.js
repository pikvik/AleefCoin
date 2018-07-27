
import React from 'react';
import { NavLink } from 'react-router-dom';
import Notifications, { notify } from 'react-notify-toast';
import { API_BASE_URL } from '../Common/apiUrl';
import axios from 'axios';
import { ScaleLoader } from 'react-spinners';
import Pagination from 'react-js-pagination';
import ToggleMenu from '../Common/togglemenu';

class purchaseList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            purchaseList: [],
            page: 1,
            perPage: 10,
            errors: { oldPassword: '', password: '', confirmPassword: '' },
            oldPasswordValid: false,
            passwordValid: false,
            confirmPasswordValid: false,
            sessionInfo: [],
            secretPin: ''
        }
        if (sessionStorage.getItem('loginInfo') == null) {
            props.history.push('/login');
        }
        this.getPurchaseInfo = this.getPurchaseInfo.bind(this);
        this.handlePageChange = this.handlePageChange.bind(this);
        this.logOut = this.logOut.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.getStatus = this.getStatus.bind(this);
        this.toggleResetPwd = this.toggleResetPwd.bind(this);
    }
    componentDidMount() {
        if (sessionStorage.getItem('loginInfo') != null) {
            let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
            this.getPurchaseInfo(sessionInfo);
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
            'sessionId': info.sessionId,
            'etherWalletAddress': info.etherWalletAddress
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
    handlePageChange(page) {
        this.setState({ page })
    }
    getStatus(value) {
        switch (value) {
            case 1:
                return 'Success';
            case 0:
                return 'Pending';
        }
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
                        <section className="wrapper">
                            <div className="dashboard-title title-inline">
                                <h1>User
                        <span> Purchase List</span>
                                </h1>
                            </div>
                            <section className="admin-content">
                                <div className="data-menchorcoin">
                                    <div className="row">
                                        <div className="col-md-12">
                                            <div role="tabpanel" className="tab-pane active" id="all">
                                                <div className="table-responsive">
                                                    {this.state.purchaseList.length > 0
                                                        ? <table className="table">
                                                            <thead>
                                                                <tr>
                                                                    <th>User Name</th>
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
                                                                )
                                                                }
                                                            </tbody>
                                                        </table >
                                                        : <span>No Purchase List Yet</span>
                                                    }
                                                    <div>
                                                        {this.state.purchaseList.length > 0 &&
                                                            <Pagination
                                                                activepage={this.state.page}
                                                                itemCountPerPage={this.state.perPage}
                                                                totalitemsCount={this.state.purchaseList.length}
                                                                pageRangeDisplayed={5}
                                                                onChange={this.hanlePageChange} />
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
                                                <input type="password" className="form-control" placeholder="Old-Password" name="oldPassword" value={this.state.oldPassword} onChange={this.handleChange} />
                                                <div style={{ color: 'red' }}>{this.state.errors.oldPassword}</div>
                                            </div>
                                            <div className="form-group">
                                                <input type="password" className="form-control" placeholder="New-Password" name="password" value={this.state.password} onChange={this.handleChange} />
                                                <div style={{ color: 'red' }}>{this.state.errors.password}</div>
                                            </div>
                                            <div className="form-group">
                                                <input type="password" className="form-control" placeholder="Confirm-password" name="confirmPassword" value={this.state.confirmPassword} onChange={this.handleChange} />
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
            </div>
        )
    }
}
export default purchaseList;