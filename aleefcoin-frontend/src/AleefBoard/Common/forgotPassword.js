import React from 'react';
import queryString from 'query-string'
import { API_BASE_URL, API_BASE_URL_VINAY } from '../Common/apiUrl';
import Notifications, { notify } from 'react-notify-toast';
import axios from 'axios';
import { NavLink } from 'react-router-dom';
import GoogleLogin from 'react-google-login';
import FacebookLogin from 'react-facebook-login';
import { ScaleLoader } from 'react-spinners';
import validator from 'validator';

class ForgotPassword extends React.Component {
    constructor(props) {
        super(props)
        const parsed = queryString.parse(props.location.search);
        this.state = {
            formValid: false,
            password: "",
            confirmPassword: "",
            errors: { password: '', confirmPassword: '' },
            emailId: parsed.emailId,
            token: parsed.token,
            loading: false,
            confirmPasswordValid: false,
            passwordValid: false,
            secretpin: ''
        }
        this.handleChangePwd = this.handleChangePwd.bind(this);
        this.toggleChangePwd = this.toggleChangePwd.bind(this);
    }
    componentWillMount() {
        if (this.state.emailId && this.state.token) {
            let payload = {
                emailId: this.state.emailId,
                token: this.state.token,
            }
            const forgetPwdUrl = API_BASE_URL + "forgot/password/emailLinkVerification";

            axios.post(forgetPwdUrl, payload)
                .then(response => {
                    if (response.status == 200) {
                        sessionStorage.setItem('verificationdata', JSON.stringify(response.data.linkVerificationInfo));
                        notify.show(response.data.message, "success");
                    }
                    else if (response.status == 206) {
                        notify.show(response.data.message, "error");
                    }
                })
                .catch(function (error) {
                });
        }


    }
    handleChangePwd(e) {
        const value = e.target.value;
        const name = e.target.name;
        this.setState({ [name]: value },
            () => { this.validateField(name, value) });
    }
    validateField(fieldName, value) {
        let fieldValidationErrors = this.state.errors;
        let confirmPasswordValid = this.state.confirmPasswordValid;
        let passwordValid = this.state.passwordValid;
        if (fieldName === 'password') {
            fieldValidationErrors.password = value.length > 7 ? '' : 'Must contain minimum 8 character';
            if (this.state.confirmPassword != "") {

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
        else if (fieldName === 'secretpin') {
            if (!validator.isNumeric(this.state.secretpin)) {
                fieldValidationErrors.secretpin = 'Secret pin must be number';
            } else {
                fieldValidationErrors.secretpin = '';

            }
        }

        this.setState({
            errors: fieldValidationErrors,
            confirmPasswordValid: confirmPasswordValid,
            passwordValid: passwordValid
        }, this.validateForm);
    }
    toggleChangePwd(event) {
        event.preventDefault();
        let sessionInfo = JSON.parse(sessionStorage.getItem('verificationdata'));
        const payload = {
            emailId: sessionInfo.emailId,
            token: sessionInfo.token,
            password: this.state.password,
            confirmPassword: this.state.confirmPassword,
            securityKey: this.state.secretpin
        }
        this.setState({ loading: true });
        const changePwdUrl = API_BASE_URL + "forgot/password/reset";
        axios.post(changePwdUrl, payload)
            .then(response => {
                this.setState({ loading: false });

                if (response.status == 200) {
                    this.props.history.push("/login");
                    notify.show(response.data.message, "success");
                }
                if (response.status == 206) {
                    notify.show(response.data.message, "error");
                }
                else if (response.data.message == 'Session Expired') {
                    this.props.history.push('/login');
                    notify.show(response.data.message, "error");
                }
            })
            .catch(function (error) {

            });
        this.setState({ password: "", confirmPassword: "" })
    }
    render() {
        return (
            // < !--- ForgotPassword-- >
            <div className="aleef-signin">
                {this.state.loading && <div className='loaderBg'>
                    <div className='loaderimg'>
                        <ScaleLoader
                            size={180}
                            color={'#fff'}
                            loading={this.state.loading}
                        />
                    </div>
                </div>}
                <Notifications />
                <div className="aleef-signin-left">
                    <div className="aleef-logo">
                        <img src="src/public/image/main-logo.png" />
                    </div>
                </div>
                <div className="aleef-signin-right">
                    <div className="aleef-container">
                        <div className="aleef-signin-form">
                            <h1 className="text-center">Reset Password</h1>
                            <form onSubmit={this.toggleChangePwd}>
                                <div className="form-group">
                                    <label>
                                        New Password
                            </label>
                                    <input type="password" placeholder=".........." name='password' value={this.state.password} onChange={this.handleChangePwd} />
                                    <div style={{ color: 'yellow' }}>{this.state.errors.password}</div>
                                </div>
                                <div className="form-group">
                                    <label>
                                        Confirm Password
                            </label>
                                    <input type="password" placeholder=".........." name='confirmPassword' value={this.state.confirmPassword} onChange={this.handleChangePwd} />
                                    <div style={{ color: 'yellow' }}>{this.state.errors.confirmPassword}</div>
                                </div>
                                <div className="form-group">
                                    <label>
                                        Secret Pin
                            </label>
                                    <input type="password" placeholder=".........." name='secretpin' value={this.state.secretpin} onChange={this.handleChangePwd} />
                                    <div style={{ color: 'yellow' }}>{this.state.errors.secretpin}</div>
                                </div>
                                <div className="form-group text-center">
                                    <button type="submit" className="aleef-signin-btn" disabled={this.state.formValid}>Confirm</button>
                                </div>

                            </form>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default ForgotPassword;