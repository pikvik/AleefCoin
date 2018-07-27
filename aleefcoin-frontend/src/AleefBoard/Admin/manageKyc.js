import React from 'react';
import AleefBoard from '../Common/aleefBoard';
import { NavLink } from 'react-router-dom';
import axios from 'axios';
import { ScaleLoader } from 'react-spinners';
import Notifications, { notify } from 'react-notify-toast';
import { API_BASE_URL } from '../Common/apiUrl';
import Pagination from 'react-js-pagination';
import ToggleMenu from '../Common/togglemenu';

class ManageKyc extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            isPopup: false,
            kycList: [],
            kycDetails: '',
            errorResponse: "",
            page: 1,
            perPage: 10,
            loading: false,
            errors: { oldPassword: '', password: '', confirmPassword: '' },
            oldPasswordValid: false,
            passwordValid: false,
            confirmPasswordValid: false,
            sessionInfo: [],
            secretPin: '',
            searchFilter: '',
            filterValue: ''
        }
        if (sessionStorage.getItem('loginInfo') == null) {
            props.history.push('/login');
        }
        this.togglePopup = this.togglePopup.bind(this);
        this.KycStatus = this.KycStatus.bind(this);
        this.KycStatusReverse = this.KycStatusReverse.bind(this);
        this.statusUpdate = this.statusUpdate.bind(this);
        this.viewKycDetails = this.viewKycDetails.bind(this);
        this.statusColor = this.statusColor.bind(this);
        this.handlePageChange = this.handlePageChange.bind(this);
        this.logOut = this.logOut.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.toggleResetPwd = this.toggleResetPwd.bind(this);
        this.filterFunc = this.filterFunc.bind(this);
        this.handleChangedrop = this.handleChangedrop.bind(this);
    }
    componentDidMount() {
        if (sessionStorage.getItem('loginInfo') != null) {
            let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
            this.getKycList(sessionInfo.loginInfo);
        }
    }
    statusColor(status) {
        switch (status) {
            case (0):
                return 'view-pen-btn';
            case (1):
                return 'view1-btn';
            case (2):
                return 'viewcan-btn';
        }
    }
    KycStatus(status) {
        switch (status) {
            case (2):
                return 'Pending';
            case (1):
                return 'Approved';
            case (0):
                return 'Rejected';
        }
    }
    KycStatusReverse(status) {
        switch (status) {
            case ('pending'):
                return '2';
            case ('approved'):
                return '1';
            case ('rejected'):
                return '0';
        }
    }
    handlePageChange(page) {
        this.setState({ page })
    }
    getKycList(props) {
        let payload = {
            'sessionId': props.sessionId
        }
        this.setState({ sessionInfo: props })
        const kycListUrl = API_BASE_URL + "list/kyc";
        this.setState({ loading: true });
        axios.post(kycListUrl, payload)
            .then(res => {
                this.setState({ loading: false });
                if (res.status === 200) {
                    this.setState({ kycList: res.data.kycList });
                } else if (res.data.message === 'Session expired!') {
                    sessionStorage.removeItem('loginInfo');
                    this.props.history.push('/login');
                }
                else if (res.data.message === 'Session expired!') {
                    sessionStorage.removeItem('loginInfo');
                    this.props.history.push('/login');
                }
            })
    }
    handleChangedrop(e) {
        this.setState({ filterValue: e.target.value });
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
    filterFunc() {
        if (sessionStorage.getItem('loginInfo') != null) {
            let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
            if (this.state.filterValue == 'userName') {
                let payload = {
                    'sessionId': sessionInfo.loginInfo.sessionId,
                    'fullName': this.state.searchFilter
                }
                this.setState({ loading: true })
                const apiBaseUrl = API_BASE_URL + "filter/kyc/username";
                axios.post(apiBaseUrl, payload)
                    .then(Response => {
                        this.setState({ loading: false })
                        if (Response.status == '200') {
                            this.setState({ kycList: Response.data.kycList });
                        } else {
                            if (response.data.message == 'Session Expired') {
                                this.props.history.push('/login');
                                notify.show(Response.data.message, 'error');
                            }
                        }
                    });
            }
            else if (this.state.filterValue == 'kycStatus') {
                let payload = {
                    'sessionId': sessionInfo.loginInfo.sessionId,
                    'kycStatus': this.KycStatusReverse(this.state.searchFilter.toLowerCase())
                }
                this.setState({ loading: true })
                const apiBaseUrl = API_BASE_URL + "filter/kyc/status";
                axios.post(apiBaseUrl, payload)
                    .then(Response => {
                        this.setState({ loading: false })
                        if (Response.status == '200') {
                            this.setState({ kycList: Response.data.kycList });

                        } else {
                            if (response.data.message == 'Session Expired') {
                                this.props.history.push('/login');
                                notify.show(Response.data.message, 'error');
                            }
                        }
                    });
            }
            else if (this.state.filterValue == 'emailId') {
                let payload = {
                    'sessionId': sessionInfo.loginInfo.sessionId,
                    'emailId': this.state.searchFilter
                }
                this.setState({ loading: true })
                const apiBaseUrl = API_BASE_URL + "filter/kyc/username";
                axios.post(apiBaseUrl, payload)
                    .then(Response => {
                        this.setState({ loading: false })
                        if (Response.status == '200') {
                            this.setState({ loading: false })
                            this.setState({ kycList: Response.data.kycList });

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
    viewKycDetails(props) {
        let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
        let payload = {
            'sessionId': sessionInfo.loginInfo.sessionId,
            'kycId': props
        }
        const kycViewUrl = API_BASE_URL + "view/kyc";
        this.setState({ loading: true });
        axios.post(kycViewUrl, payload)
            .then(res => {
                this.setState({ loading: false });
                if (res.status === 200) {
                    this.setState({ kycDetails: res.data.kycUserInfo });
                } else {
                    if (res.data.message === 'Session expired!') {
                        sessionStorage.removeItem('loginInfo');
                        this.props.history.push('/login');
                        notify.show(res.data.message, 'error');
                    }
                }
            })
    }

    statusUpdate(event) {
        let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
        let payload = {
            'sessionId': sessionInfo.loginInfo.sessionId,
            'id': this.state.kycDetails.id,
            'kycStatus': event.target.name
        }
        const statusUpdateUrl = API_BASE_URL + "update/kycstatus";
        this.setState({ loading: true });

        axios.post(statusUpdateUrl, payload)
            .then(res => {
                this.setState({ loading: false });
                this.setState({ isPopup: !this.state.isPopup });
                if (res.status === 200) {
                    this.getKycList(sessionInfo.loginInfo);
                    notify.show(res.data.message, 'success');
                } else if (res.status === 206) {
                    notify.show(res.data.message, 'error');
                    if (res.data.message === 'Session expired!') {
                        sessionStorage.removeItem('loginInfo');
                        this.props.history.push('/login');
                        notify.show(res.data.message, 'error');
                    }
                }
            })
    }
    togglePopup() {
        this.setState({ isPopup: !this.state.isPopup });
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
                                <h1>Manage
                <span> KYC Details</span>
                                </h1>
                                <div className="searchbox-select">
                                    <form>
                                        <select value={this.state.filterValue} onChange={this.handleChangedrop} >
                                            <option value="">--Select Filter--</option>
                                            <option value="userName">User Name</option>
                                            <option value="kycStatus">Kyc Status</option>
                                            <option value="emailId">Email ID</option>
                                        </select>
                                        <div className="searchinput">
                                            <input type="text" className="form-control" placeholder="Search here" name="searchFilter" value={this.state.searchFilter || ''} onChange={this.handleChange} />
                                            <i className="fa fa-search" style={{ cursor: 'pointer' }} aria-hidden="true" onClick={this.filterFunc}></i>
                                        </div>
                                    </form>
                                </div>
                            </div>
                            <section className="admin-content">
                                <div className="manage-kyc-table">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>ID</th>
                                                <th>User ID</th>
                                                <th>Email ID</th>
                                                <th>Date of Birth</th>
                                                <th>Status</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {this.state.kycList.map((each, i) =>
                                                this.state.page * this.state.perPage > i &&
                                                (this.state.page - 1) * this.state.perPage <= i &&
                                                <tr key={i}>
                                                    <td>{each.id}</td>
                                                    <td>{each.fullName}</td>
                                                    <td>{each.emailId}</td>
                                                    <td>{each.dob}</td>
                                                    <td >
                                                        <span className={this.statusColor(each.kycStatus)}>{this.KycStatus(each.kycStatus)}</span>
                                                    </td>

                                                    <td>
                                                        <button type="button" className="view-btn" onClick={() => {
                                                            this.togglePopup();
                                                            this.viewKycDetails(each.id);
                                                        }}>View</button>
                                                    </td>
                                                </tr>
                                            )}
                                        </tbody>
                                    </table>
                                    <div>
                                        {this.state.kycList.length > 0 &&
                                            <Pagination
                                                activePage={this.state.page}
                                                itemsCountPerPage={this.state.perPage}
                                                totalItemsCount={this.state.kycList.length}
                                                pageRangeDisplayed={5}
                                                onChange={this.handlePageChange} />
                                        }
                                    </div>
                                </div>

                            </section>
                        </section>
                    </section>
                </section>
                {this.state.isPopup && <div className="kycViewBg" style={{ top: '10px', background: 'rgba(0,0,0,0.7)' }}>
                    <div className="kycViewBgPop">
                        <div className="kycViewBgPophead" style={{ marginBottom: '20px' }}>
                            <h1>Manage KYC Details</h1>
                            <button className="btn-close btnCloseBg" onClick={this.togglePopup}>X</button>
                        </div>
                        <div className="kycViewBgPopbody">
                            <form name="kycForm">
                                <div className="form-group">
                                    <div className="col-md-6">
                                        <div className="kycInfoBg"><span className="kycInfoTitle">Full Name</span><span className="kycInfoContent">{this.state.kycDetails.fullName}</span></div>
                                    </div>
                                    <div className="col-md-6">

                                        <div className="kycInfoBg"><span className="kycInfoTitle">Date Of Birth</span><span className="kycInfoContent">{this.state.kycDetails.dob}</span></div>
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-md-6">
                                        <div className="kycInfoBg"><span className="kycInfoTitle">Gender</span><span className="kycInfoContent">{this.state.kycDetails.gender}</span></div>
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-md-6">
                                        <div className="kycInfoBg"><span className="kycInfoTitle">Address</span><span className="kycInfoContent">{this.state.kycDetails.address}</span></div>
                                    </div>
                                    <div className="col-md-6">
                                        <div className="kycInfoBg"><span className="kycInfoTitle">City/State</span><span className="kycInfoContent">{this.state.kycDetails.city}</span></div>
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-md-6">
                                        <div className="kycInfoBg"><span className="kycInfoTitle">Country</span><span className="kycInfoContent">{this.state.kycDetails.country}</span></div>
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-md-6">
                                        <div className="kycInfoBg"><span className="kycInfoTitle">Phone No.</span><span className="kycInfoContent">{this.state.kycDetails.mobileNo}</span></div>
                                    </div>
                                    <div className="col-md-6">
                                        <div className="kycInfoBg"><span className="kycInfoTitle">Email Id</span><span className="kycInfoContent">{this.state.kycDetails.emailId}</span></div>
                                    </div>
                                </div>

                                <div className="form-group">
                                    <div className="col-md-6">

                                        <div className="kycInfoBg"><span className="kycInfoTitle">Photo ID</span><span className="kycInfoContent"><a download target="_blank" href={this.state.kycDetails.kycDoc1Path} >Document-1</a></span></div>
                                    </div>
                                    <div className="col-md-6">

                                        <div className="kycInfoBg"><span className="kycInfoTitle">Residential ID</span><span className="kycInfoContent"><a download target="_blank" href={this.state.kycDetails.kycDoc2Path} >Document-2</a></span></div>
                                    </div>
                                </div>
                                <div className="btnBgDiv text-right">
                                    <button type="button" name='1' className="approv buttonWid" onClick={this.statusUpdate}>Approved</button>
                                    <button type="button" name='0' className="reject buttonWid cancelbtn" onClick={this.statusUpdate}>Rejected</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>}
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
export default ManageKyc;