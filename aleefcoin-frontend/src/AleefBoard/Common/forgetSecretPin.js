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

class ForgetSecretpin extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            formValid: false,
            password: "",
            secpin: "",
            confSecpin: '',
            emailId: '',
            errors: { password: '', secpin: '' },
            loading: false,
            confSecpinValid: false,
            passwordValid: false,
            secretpin: ''
        }
        this.handleChangePwd = this.handleChangePwd.bind(this);
        this.toggleChangeSecPin = this.toggleChangeSecPin.bind(this);
    }

    handleChangePwd(e) {
        const value = e.target.value;
        const name = e.target.name;
        this.setState({ [name]: value },
            () => { this.validateField(name, value) });
    }
    validateField(fieldName, value) {
        let fieldValidationErrors = this.state.errors;
        let confSecpinValid = this.state.confSecpinValid;
        let passwordValid = this.state.passwordValid;
        if (fieldName === 'secpin') {
            fieldValidationErrors.secpin = value.length < 7 ? '' : 'Must contain 6 numbers only';
            if (this.state.confSecpin != "") {
                if (value != this.state.confSecpin) {
                    fieldValidationErrors.confSecpin = 'Pin does not match';
                } else {
                    fieldValidationErrors.confSecpin = '';
                }
            }
        } else if (fieldName === 'confSecpin') {
            if (value != this.state.secpin) {
                fieldValidationErrors.confSecpin = 'Pin does not match';
            } else {
                fieldValidationErrors.confSecpin = '';
            }
        }
        if (fieldName === 'password') {
            fieldValidationErrors.password = value.length > 7 ? '' : 'Must contain Minimum 8 characters';
        } else {
            fieldValidationErrors.password = '';
        }
        if (fieldName === 'emailId') {
            fieldValidationErrors.emailId = value.match(/^([\w.%+-]+)@([\w-]+\.)+([\w]{2,})$/i) ? '' : 'Enter Valid Email Id';
        }
        else {
            fieldValidationErrors.emailId = '';
        }
        this.setState({
            errors: fieldValidationErrors,
            confSecpinValid: confSecpinValid,
            passwordValid: passwordValid
        }, this.validateForm);
    }
    toggleChangeSecPin(event) {
        this.setState({ isPwdChange: false })
        event.preventDefault();
        const payload = {
            emailId: this.state.emailId,
            password: this.state.password,
            confirmSecurityKey: this.state.confSecpin,
            securityKey: this.state.secpin
        }
        this.setState({ loading: true });
        const changePwdUrl = API_BASE_URL + "reset/security/pin";
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
        this.setState({ password: "", confSecpin: "", emailId: '', secpin: '' })
    }
    render() {
        return (
            // < !--- ForgetSecretpin-- >
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
                            <h1 className="text-center">Reset Secret Pin</h1>
                            <form onSubmit={this.toggleChangeSecPin}>
                                <div className="form-group">
                                    <label>
                                        New Secret Pin
                            </label>
                                    <input type="password" placeholder=".........." name='secpin' value={this.state.secpin} onChange={this.handleChangePwd} />
                                    <div style={{ color: 'yellow' }}>{this.state.errors.secpin}</div>
                                </div>
                                <div className="form-group">
                                    <label>
                                        Confirm Secret Pin
                            </label>
                                    <input type="password" placeholder=".........." name='confSecpin' value={this.state.confSecpin} onChange={this.handleChangePwd} />
                                    <div style={{ color: 'yellow' }}>{this.state.errors.confSecpin}</div>
                                </div>
                                <div className="form-group">
                                    <label>
                                        Email Id
                            </label>
                                    <input type="text" placeholder=".........." name='emailId' value={this.state.emailId} onChange={this.handleChangePwd} />
                                    <div style={{ color: 'yellow' }}>{this.state.errors.emailId}</div>
                                </div>
                                <div className="form-group">
                                    <label>
                                        Password
                            </label>
                                    <input type="password" placeholder=".........." name='password' value={this.state.password} onChange={this.handleChangePwd} />
                                    <div style={{ color: 'yellow' }}>{this.state.errors.password}</div>
                                </div>
                                <div className="form-group text-center">
                                    <button type="submit" className="aleef-signin-btn" disabled={this.state.secpin == '' || this.state.confSecpin == '' || this.state.emailId == '' || this.state.password == ''}>Confirm</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default ForgetSecretpin;