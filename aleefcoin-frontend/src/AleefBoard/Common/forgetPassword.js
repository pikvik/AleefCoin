import React from 'react';
import { API_BASE_URL } from '../Common/apiUrl';
import Notifications, { notify } from 'react-notify-toast';
import axios from 'axios';
import queryString from 'query-string';
import { ScaleLoader } from 'react-spinners';
import { NavLink } from 'react-router-dom';

class ForgetPassword extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            emailId: "",
            emailValid: false,
            formValid: false,
            password: "",
            confirmPassword: "",
            errors: { password: '', confirmPassword: '' },
            loading: false,
            confirmPasswordValid: false,
            passwordValid: false,
            isPwdChange: false
        }

        this.handleChange = this.handleChange.bind(this);
        this.toggleForgetPwd = this.toggleForgetPwd.bind(this);
    }

    handleChange(e) {
        const value = e.target.value;
        const name = e.target.name;
        this.setState({ [name]: value },
            () => { this.validateField(name, value) });
    }
    validateField(fieldName, value) {
        let fieldValidationErrors = this.state.errors;
        let emailValid = this.state.emailValid;

        switch (fieldName) {
            case 'emailId':
                emailValid = value.match(/^([\w.%+-]+)@([\w-]+\.)+([\w]{2,})$/i);
                fieldValidationErrors.emailId = emailValid ? '' : ' Please enter valid email id';
                break;
            default:
                break;
        }

        this.setState({
            errors: fieldValidationErrors,
            emailValid: emailValid,
        }, this.validateForm);
    }
    validateForm() {
        this.setState({ formValid: this.state.emailValid });
    }

    toggleForgetPwd() {
        let payload = {
            "emailId": this.state.emailId
        }
        this.setState({ loading: true });
        const apiBaseUrl = API_BASE_URL + "forgot/password/emailVerification";
        axios.post(apiBaseUrl, payload)
            .then(response => {
                this.setState({ loading: false });
                if (response.status == 200) {
                    this.props.history.push('/login');
                    notify.show(response.data.message, "success");
                }
                else if (response.status == 206) {
                    notify.show(response.data.message, "error");
                }
                else if (response.data.message == "Session Expired") {
                    notify.show(response.data.message, "error");
                }
            })
    }

    render() {
        return (
            // <!---ForgetPassword-->
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
                            <h1 className="text-center">Forgot Password</h1>
                            <form>
                                <div>
                                    <div className="form-group">
                                        <label>
                                            Email
                                    </label>
                                        <input type="text" name="emailId" value={this.state.emailId} onChange={this.handleChange} placeholder="Enter Your Email" />
                                        <div style={{ color: 'yellow' }}>{this.state.errors.emailId}</div>
                                    </div>
                                    <div className="form-group text-center">
                                        <NavLink className="clickhere" to='/login' >Click here to Login</NavLink>
                                    </div>
                                    <div className="form-group text-center">
                                        <button type="button" className="aleef-signin-btn" onClick={this.toggleForgetPwd} disabled={!this.state.formValid}>Send Mail</button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
export default ForgetPassword;