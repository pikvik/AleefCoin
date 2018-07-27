import React, { Fragment } from 'react';
import { NavLink } from 'react-router-dom';
import { API_BASE_URL } from './apiUrl';
import axios from 'axios';
import Notifications, { notify } from 'react-notify-toast';

class AleefBoard extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            oldPassword: "",
            password: "",
            confirmPassword: "",
            errors: {}
        }
        this.logOut = this.logOut.bind(this);
        this.toggleResetPwd = this.toggleResetPwd.bind(this);
        this.handleChange = this.handleChange.bind(this);
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
                } else if (response.status == 206) {
                    notify.show(response.data.message, "error");
                    if (response.data.message == 'Session Expired') {
                        this.setState({ loading: false });
                        sessionStorage.removeItem('loginInfo');
                        this.props.history.push('/login');
                        notify.show(response.data.message, "error");
                    }
                }
            })
            .catch(function (error) {
                console.log(error);
            });
    }
    render() {
        return (
            <Fragment>
                <header className="header fixed-top clearfix">
                    <div className="brand">
                        <a href="#" className="logo">
                            <img src="src/public/image/aleef-token.png" /> </a>
                        <div className="sidebar-toggle-box">
                        </div>
                    </div>
                    <div className="top-nav clearfix">
                        <ul className="nav pull-right top-menu">
                            <li className="dropdown">
                                <a data-toggle="dropdown" className="dropdown-toggle" href="#">
                                    <img alt="" src="src/public/image/user.png" /> </a>
                                <ul className="dropdown-menu extended logout">
                                    <li>
                                        <a data-toggle="modal" data-target="#resetpwd">
                                            <i className="fa fa-cog"></i> Reset Password</a>
                                    </li>
                                    <li>
                                        <a onClick={this.logOut}>
                                            <i className="fa fa-key"></i> Log Out</a>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                    </div>
                </header>
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
                                                <input type="password" className="form-control" placeholder="Old Password" name="oldPassword" value={this.state.oldPassword} onChange={this.handleChange} />
                                                <div style={{ color: 'red' }}>{this.state.errors.oldPassword}</div>
                                            </div>
                                            <div className="form-group">
                                                <input type="password" className="form-control" placeholder="New Password" name="password" value={this.state.password} onChange={this.handleChange} />
                                                <div style={{ color: 'red' }}>{this.state.errors.password}</div>
                                            </div>
                                            <div className="form-group">
                                                <input type="password" className="form-control" placeholder="Confirm New password" name="confirmPassword" value={this.state.confirmPassword} onChange={this.handleChange} />
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
            </Fragment>
        )
    }
}
export default AleefBoard;
