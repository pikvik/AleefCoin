import React from 'react';
import { NavLink } from 'react-router-dom';
import Notifications, { notify } from 'react-notify-toast';
import { API_BASE_URL } from '../Common/apiUrl';
import axios from 'axios';
import { ScaleLoader } from 'react-spinners';
import Pagination from 'react-js-pagination';
import ToggleMenu from '../Common/togglemenu';

class userList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            loading: false,
            userLists: [],
            page: 1,
            sendPage: 1,
            recievePage: 1,
            perPage: 10,
            errors: { oldPassword: '', password: '', confirmPassword: '' },
            oldPasswordValid: false,
            passwordValid: false,
            confirmPasswordValid: false,
            sessionInfo: [],
            secretPin: '',
            searchFilter: '',
            isPopup: false
        }
        if (sessionStorage.getItem('loginInfo') == null) {
            props.history.push('/login');
        }
        this.logOut = this.logOut.bind(this);
        this.getUserlist = this.getUserlist.bind(this);
        this.getStatusdetails = this.getStatusdetails.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.receivePagechange = this.receivePagechange.bind(this);
        this.toggleResetPwd = this.toggleResetPwd.bind(this);
        this.filterFunc = this.filterFunc.bind(this);
        this.togglePopup = this.togglePopup.bind(this);
    }
    componentDidMount() {
        if (sessionStorage.getItem('loginInfo') != null) {
            let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
            this.getUserlist(sessionInfo);

        }
    }
    getUserlist() {
        let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
        let payload = {
            'sessionId': sessionInfo.loginInfo.sessionId
        }
        this.setState({ loading: true });
        const apiUrl = API_BASE_URL + "users/list";
        axios.post(apiUrl, payload)
            .then(response => {
                this.setState({ loading: false });
                if (response.status == 200) {
                    this.setState({ userLists: response.data.listUsers });

                } else if (response.data.message == 'Session Expired') {
                    sessionStorage.removeItem('loginInfo');
                    this.props.history.push('/login');
                    notify.show(response.data.message, "error");
                }
            })

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
    filterFunc(e) {
        if (sessionStorage.getItem('loginInfo') != null) {
            let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
            if (this.state.searchFilter.match(/^([\w.%+-]+)@([\w-]+\.)+([\w]{2,})$/i)) {
                let payload = {
                    'sessionId': sessionInfo.loginInfo.sessionId,
                    'emailId': this.state.searchFilter
                }
                this.setState({ loading: true })
                const apiBaseUrl = API_BASE_URL + "user/list/filter";
                axios.post(apiBaseUrl, payload)
                    .then(Response => {
                        this.setState({ loading: false })
                        if (Response.status == '200') {
                            this.setState({ userLists: Response.data.listUsers });
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
                const apiBaseUrl = API_BASE_URL + "user/list/filter";
                axios.post(apiBaseUrl, payload)
                    .then(Response => {
                        this.setState({ loading: false })
                        if (Response.status == '200') {
                            this.setState({ userLists: Response.data.listUsers });

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
    togglePopup() {
        this.setState({ isPopup: !this.state.isPopup });
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
                        <span>List</span>
                                </h1>
                                <div className="searchbox-select">
                                    <form>
                                        <select>
                                            <option value="all">--Filter--</option>
                                            <option value="dummy">Email Id</option>
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
                                                            <tr><th>Id</th>
                                                                <th>User Name</th>
                                                                <th>Email Id</th>
                                                                <th>Date &amp; Time</th>
                                                                <th>REF-L1</th>
                                                                <th>REF-L2</th>
                                                                <th>REF-L3</th>
                                                                <th>REF-L4</th>
                                                                <th>Total REF-Bouns</th>
                                                            </tr></thead>
                                                        <tbody>
                                                            {
                                                                this.state.userLists.map((item, key) =>
                                                                    this.state.recievePage * this.state.perPage > key &&
                                                                    (this.state.recievePage - 1) * this.state.perPage <= key &&
                                                                    <tr key={key}>
                                                                        <td >{item.id}</td>
                                                                        <td>{item.userName}</td>
                                                                        <td>{item.emailId}</td>
                                                                        <td>{item.createdDate}</td>
                                                                        <td>{item.referralLevel1Tokens}</td>
                                                                        <td>{item.referralLevel2Tokens}</td>
                                                                        <td>{item.referralLevel3Tokens}</td>
                                                                        <td>{item.referralLevel4Tokens}</td>
                                                                        <td>{item.referralTokens}</td>
                                                                    </tr>
                                                                )
                                                            }

                                                        </tbody>
                                                    </table >
                                                    <div>
                                                        {this.state.userLists.length > 0 &&
                                                            <Pagination
                                                                activePage={this.state.recievePage}
                                                                itemsCountPerPage={this.state.perPage}
                                                                totalItemsCount={this.state.userLists.length}
                                                                pageRangeDisplayed={5}
                                                                onChange={this.receivePagechange} />
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
                {this.state.isPopup && <div class="referral-bonus">
                    <div class="referral-bonus-div">
                        <h1>Referral Bonus</h1>
                        <div class="burn-body-cont">
                            <table className="table">
                                <thead>
                                    <tr>
                                        <th>Levels</th>
                                        <th >Bonus</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td> Level 1</td>
                                        {this.state.userLists.map((items, key) => {
                                            return (
                                                <td key={key}>{items.referralLevel1Tokens}</td>
                                            )
                                        })}
                                    </tr>
                                </tbody>
                            </table >
                            <button type="button" class="btnclose" onClick={this.togglePopup}>Close</button>
                        </div>
                    </div>
                </div>}
            </div>
        )
    }
}
export default userList;
