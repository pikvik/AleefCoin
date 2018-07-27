import React from 'react';
import { NavLink } from 'react-router-dom';
import Pagination from 'react-js-pagination';
import axios from 'axios';
import Notifications, { notify } from 'react-notify-toast';
import { API_BASE_URL } from '../Common/apiUrl';
import { ScaleLoader } from 'react-spinners';
import AleefBoard from '../Common/aleefBoard';
import ToggleMenu from '../Common/togglemenu';

class RefferdMemberList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            allRefferalLevel: [],
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
            sessionInfo: [],
            secretPin: ''
        }
        this.gatAllRefferalLevel = this.gatAllRefferalLevel.bind(this);
        this.handlePageChange = this.handlePageChange.bind(this);
        this.sendPagechange = this.sendPagechange.bind(this);
        this.getStatus = this.getStatus.bind(this);
        this.receivePagechange = this.receivePagechange.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.filterFunc = this.filterFunc.bind(this);
        this.logOut = this.logOut.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.toggleResetPwd = this.toggleResetPwd.bind(this);
    }
    componentDidMount() {
        if (sessionStorage.getItem('loginInfo') != null) {
            let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
            this.gatAllRefferalLevel(sessionInfo);
        }
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
                    'sessionId': sessionInfo.sessionId,
                    'transactionMode': this.state.searchFilter
                }
                const apiBaseUrl = API_BASE_URL + "token/transaction/filter/mode";
                axios.post(apiBaseUrl, payload)
                    .then(Response => {
                        if (Response.status == '200') {
                            this.setState({ allRefferalLevel: Response.data.purchaseListInfo });
                        } else {
                            if (response.data.message == 'Session Expired') {
                                this.props.history.push('/login');
                            }
                        }
                    });
            }
            else {
                let payload = {
                    'sessionId': sessionInfo.sessionId,
                    'etherWalletAddress': this.state.searchFilter
                }
                const apiBaseUrl = API_BASE_URL + "token/transaction/filter/address";
                axios.post(apiBaseUrl, payload)
                    .then(Response => {
                        if (Response.status == '200') {
                            this.setState({ allRefferalLevel: Response.data.purchaseListInfo });

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
    gatAllRefferalLevel(info) {
        let payload = {
            'sessionId': info.sessionId,
        }
        this.setState({ sessionInfo: info })
        this.setState({ loading: true });
        const apiBaseUrl = API_BASE_URL + "get/referrals";
        axios.post(apiBaseUrl, payload)
            .then(Response => {
                this.setState({ loading: false });
                if (Response.status == '200') {
                    this.setState({ allRefferalLevel: Response.data.allLevelsRefNames })
                } else {
                    if (Response.data.message == 'Session Expired') {
                        this.setState({ loading: false });
                        this.props.history.push('/login');
                    }
                    if (Response.status == '206') {

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
                                <h1>My
                        <span> Referrals</span>
                                </h1>
                            </div>
                            <section className="admin-content">
                                <div className="data-menchorcoin">
                                    <div className="row">
                                        <div className="col-md-12">

                                            {/* <!-- Nav tabs --> */}
                                            <div className="card">
                                                <ul className="nav nav-tabs" role="tablist">
                                                    <li role="presentation" className="active">
                                                        <a href="#Referal1" aria-controls="Referal Level 1" role="tab" data-toggle="tab">Referral Level 1</a>
                                                    </li>
                                                    <li role="presentation">
                                                        <a href="#Referal2" aria-controls="Referal Level 2" role="tab" data-toggle="tab">Referral Level 2</a>
                                                    </li>
                                                    <li role="presentation">
                                                        <a href="#Referal3" aria-controls="Referal Level 3" role="tab" data-toggle="tab">Referral Level 3</a>
                                                    </li>
                                                    <li role="presentation">
                                                        <a href="#Referal4" aria-controls="Referal Level 4" role="tab" data-toggle="tab">Referral Level 4</a>
                                                    </li>
                                                </ul>

                                                {/* <!-- Tab panes --> */}
                                                <div className="tab-content">
                                                    <div role="tabpanel" className="tab-pane active" id="Referal1">
                                                        <div className="table-responsive">
                                                            {this.state.allRefferalLevel[0]
                                                                ? <table className="table">
                                                                    <thead>
                                                                        <tr>
                                                                            <th> User Name </th>
                                                                        </tr>
                                                                    </thead>
                                                                    <tbody>
                                                                        {
                                                                            this.state.allRefferalLevel[0].map((item, key) =>
                                                                                this.state.page * this.state.perPage > key &&
                                                                                (this.state.page - 1) * this.state.perPage <= key &&
                                                                                <tr key={key}>
                                                                                    <td>{item}</td>
                                                                                </tr>
                                                                            )
                                                                        }
                                                                    </tbody>
                                                                </table >
                                                                : <span> Not Available </span>
                                                            }
                                                        </div>
                                                        <div>
                                                            {this.state.allRefferalLevel[0] &&
                                                                <Pagination
                                                                    activePage={this.state.page}
                                                                    itemsCountPerPage={this.state.perPage}
                                                                    totalItemsCount={this.state.allRefferalLevel[0].length}
                                                                    pageRangeDisplayed={5}
                                                                    onChange={this.handlePageChange} />
                                                            }
                                                        </div>
                                                    </div>
                                                    <div role="tabpanel" className="tab-pane" id="Referal2">
                                                        <div className="table-responsive">
                                                            {this.state.allRefferalLevel[1]
                                                                ? <table className="table">
                                                                    <thead>
                                                                        <tr><th> User Name </th>
                                                                        </tr>
                                                                    </thead>
                                                                    <tbody>
                                                                        {
                                                                            this.state.allRefferalLevel[1].map((item, key) =>
                                                                                this.state.sendPage * this.state.perPage > key &&
                                                                                (this.state.sendPage - 1) * this.state.perPage <= key &&
                                                                                <tr key={key}>
                                                                                    <td>{item}</td>
                                                                                </tr>
                                                                            )
                                                                        }
                                                                    </tbody>
                                                                </table >
                                                                : <span> Not Available </span>
                                                            }
                                                        </div>
                                                        <div>
                                                            {this.state.allRefferalLevel[1] &&
                                                                <Pagination
                                                                    activePage={this.state.sendPage}
                                                                    itemsCountPerPage={this.state.perPage}
                                                                    totalItemsCount={this.state.allRefferalLevel[1].length}
                                                                    pageRangeDisplayed={5}
                                                                    onChange={this.sendPagechange} />
                                                            }
                                                        </div>

                                                    </div>
                                                    <div role="tabpanel" className="tab-pane" id="Referal3">
                                                        <div className="table-responsive">
                                                            {this.state.allRefferalLevel[2]
                                                                ? <table className="table">
                                                                    <thead>
                                                                        <tr><th> User Name </th>
                                                                        </tr>
                                                                    </thead>
                                                                    <tbody>
                                                                        {
                                                                            this.state.allRefferalLevel[2].map((item, key) =>
                                                                                this.state.recievePage * this.state.perPage > key &&
                                                                                (this.state.recievePage - 1) * this.state.perPage <= key &&
                                                                                <tr key={key}>
                                                                                    <td>{item}</td>
                                                                                </tr>
                                                                            )
                                                                        }
                                                                    </tbody>
                                                                </table >
                                                                : <span> Not Available </span>
                                                            }
                                                        </div>
                                                        <div>
                                                            {this.state.allRefferalLevel[2] &&
                                                                <Pagination
                                                                    activePage={this.state.recievePage}
                                                                    itemsCountPerPage={this.state.perPage}
                                                                    totalItemsCount={this.state.allRefferalLevel[2].length}
                                                                    pageRangeDisplayed={5}
                                                                    onChange={this.receivePagechange} />
                                                            }
                                                        </div>
                                                    </div>
                                                    <div role="tabpanel" className="tab-pane" id="Referal4">
                                                        <div className="table-responsive">
                                                            {this.state.allRefferalLevel[3]
                                                                ? <table className="table">
                                                                    <thead>
                                                                        <tr><th> User Name </th>
                                                                        </tr>
                                                                    </thead>
                                                                    <tbody>
                                                                        {
                                                                            this.state.allRefferalLevel[3].map((item, key) =>
                                                                                this.state.recievePage * this.state.perPage > key &&
                                                                                (this.state.recievePage - 1) * this.state.perPage <= key &&
                                                                                <tr key={key}>
                                                                                    <td>{item}</td>
                                                                                </tr>
                                                                            )
                                                                        }
                                                                    </tbody>
                                                                </table >
                                                                : <span> Not Available </span>
                                                            }
                                                        </div>
                                                        <div>
                                                            {this.state.allRefferalLevel[3] &&
                                                                <Pagination
                                                                    activePage={this.state.recievePage}
                                                                    itemsCountPerPage={this.state.perPage}
                                                                    totalItemsCount={this.state.allRefferalLevel[3].length}
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

export default RefferdMemberList;