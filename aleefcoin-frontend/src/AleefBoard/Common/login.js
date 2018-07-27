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

class Login extends React.Component {
    constructor(props) {
        super(props)
        const parsed = queryString.parse(props.location.search);
        this.state = {
            emailId: '',
            password: '',
            etherWalletPassword: '',
            confirmEtherWalletPassword: '',
            errors: { emailId: '', password: '' },
            errorsPop: { emailId: '', etherWalletPassword: '', confirmEtherWalletPassword: '' },
            emailIdValid: false,
            passwordValid: false,
            etherWalletPasswordValid: false,
            confirmEtherWalletPasswordValid: false,
            formValid: false,
            confMailid: parsed.emailId,
            resetPwdEmailId: parsed.emailId,
            token: parsed.token,
            loading: false,
            isSocialLoginPop: false,
            isOtp: false,
            otp: '',
            formValid: false
        }
        this.handleChange = this.handleChange.bind(this);
        this.handleChangePop = this.handleChangePop.bind(this);
        this.loginAleef = this.loginAleef.bind(this);
        this.googleSignIn = this.googleSignIn.bind(this);
        this.responseFacebook = this.responseFacebook.bind(this);
        this.verifyOtp = this.verifyOtp.bind(this);
        this.pophandleSubmit = this.pophandleSubmit.bind(this);
    }
    componentWillMount() {
        if (this.state.confMailid) {
            let payload = {
                emailId: this.state.confMailid
            }
            const emailverify = API_BASE_URL + "emailVerification";
            axios.post(emailverify, payload)
                .then(response => {
                    if (response.status == 200) {
                        notify.show(response.data.message, 'success');
                        this.props.history.push('/sucess');
                    }
                    else if (response.data.message == 'Session Expired') {
                        this.props.history.push('/login');
                    }
                    notify.show(response.data.message, "error");
                })
                .catch(function (error) {
                    console.log(error);
                });
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
        let emailIdValid = this.state.emailIdValid;
        let passwordValid = this.state.passwordValid;

        switch (fieldName) {
            case 'emailId':
                emailIdValid = value.match(/^([\w.%+-]+)@([\w-]+\.)+([\w]{2,})$/i);
                fieldValidationErrors.emailId = emailIdValid ? '' : ' Please enter valid email id';
                break;
            case 'password':
                passwordValid = value.length >= 8;
                fieldValidationErrors.password = passwordValid ? '' : 'Must contain minimum 8 characters';
                break;
            default:
                break;
        }

        this.setState({
            errors: fieldValidationErrors,
            emailIdValid: emailIdValid,
            passwordValid: passwordValid
        }, this.validateForm);
    }
    validateForm() {
        this.setState({ formValid: this.state.emailIdValid && this.state.passwordValid });
    }

    handleChangePop(e) {
        const value = e.target.value;
        const name = e.target.name;
        this.setState({ [name]: value },
            () => { this.validateFieldPop(name, value) });
    }
    validateFieldPop(fieldName, value) {
        let fieldValidationErrors = this.state.errorsPop;
        let emailIdValid = this.state.emailIdValid;
        let confirmEtherWalletPasswordValid = this.state.confirmEtherWalletPasswordValid;
        let etherWalletPasswordValid = this.state.etherWalletPasswordValid;

        switch (fieldName) {
            case 'emailId':
                emailIdValid = value.match(/^([\w.%+-]+)@([\w-]+\.)+([\w]{2,})$/i);
                fieldValidationErrors.emailId = emailIdValid ? '' : ' Please enter valid email id';
                break;
            case 'etherWalletPassword':
                etherWalletPasswordValid = value.length >= 8 && value.match(/^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\d]){1,})(?=(.*[\W]){1,})(?!.*\s).{8,}$/);
                fieldValidationErrors.etherWalletPassword = etherWalletPasswordValid ? '' : 'Must contain atleast one uppercase, one lowercase ,one number, one special character and must contain minimum 8 character';
                break;
            case 'confirmEtherWalletPassword':
                confirmEtherWalletPasswordValid = validator.equals(value, this.state.etherWalletPassword);
                fieldValidationErrors.confirmEtherWalletPassword = confirmEtherWalletPasswordValid ? '' : 'Password does not match';
                break;
            default:
                break;
        }

        this.setState({
            errorsPop: fieldValidationErrors,
            emailIdValid: emailIdValid,
            etherWalletPasswordValid: etherWalletPasswordValid,
            confirmEtherWalletPasswordValid: confirmEtherWalletPasswordValid
        }, this.validateFormPop);
    }
    validateFormPop() {
        this.setState({ formValid: this.state.emailIdValid && this.state.etherWalletPasswordValid && this.state.confirmEtherWalletPasswordValid });
    }
    responseFacebook(response) {
        let payload = {
            "userName": response.name,
            "emailId": response.email,
            "mediaId": response.userID
        }
        this.setState({ emailId: response.email })
        const apiBaseUrl = API_BASE_URL + "social/media/login";
        sessionStorage.setItem('loginInfo', JSON.stringify(response));
        axios.post(apiBaseUrl, payload)
            .then(response => {
                this.setState({ loading: false });
                if (response.status == 200) {
                    this.setState({ loading: false });
                    sessionStorage.setItem("logininfo", JSON.stringify(response.data.loginInfo));
                    let logininfo = JSON.parse(sessionStorage.getItem('logininfo'));
                    if (logininfo.popUpStatus == 'true') {
                        this.setState({ isSocialLoginPop: true })
                    } else if (logininfo.popUpStatus == 'false') {
                        sessionStorage.setItem('loginInfo', JSON.stringify(response.data.loginInfo));
                        if (response.data.loginInfo.roleId === 3) {
                            this.props.history.push('/userDashboard');
                            notify.show(response.data.message, "success");
                        }
                    }
                }
                else if (response.status == 206) {
                    this.setState({ loading: false });
                    notify.show(response.data.message, "error");
                } else if (response.data.message == "Session Expired") {
                    this.props.history.push('/login')
                    notify.show(response.data.message, "error");
                }
            })
    }
    // google Sign in 
    googleSignIn(response) {
        let details = {
            "userName": response.profileObj.name,
            "emailId": response.profileObj.email,
            "mediaId": response.profileObj.googleId,
        }
        this.setState({ emailId: response.profileObj.email })
        sessionStorage.setItem('loginInfo', JSON.stringify(response.profileObj));
        const apiBaseUrl = API_BASE_URL + "social/media/login";
        axios.post(apiBaseUrl, details)
            .then(response => {
                this.setState({ loading: false });
                if (response.status == 200) {
                    this.setState({ loading: false });
                    sessionStorage.setItem("logininfo", JSON.stringify(response.data.loginInfo));
                    let logininfo = JSON.parse(sessionStorage.getItem('logininfo'));
                    if (logininfo.popUpStatus == 'true') {
                        this.setState({ isSocialLoginPop: true })
                    } else if (logininfo.popUpStatus == 'false') {
                        sessionStorage.setItem('loginInfo', JSON.stringify(response.data.loginInfo));
                        if (response.data.loginInfo.roleId === 3) {
                            this.props.history.push('/userDashboard');
                            notify.show(response.data.message, "success");
                        }
                    }
                }
                else if (response.status == 206) {
                    this.setState({ loading: false });
                    notify.show(response.data.message, "error");
                } else if (response.data.message == "Session Expired") {
                    this.props.history.push('/login')
                    notify.show(response.data.message, "error");
                }
            })
    }
    loginAleef(event) {
        event.preventDefault();
        let payLoad = {
            'emailId': this.state.emailId,
            'password': this.state.password
        }
        const loginUrl = API_BASE_URL + "login";
        this.setState({ loading: true });
        axios.post(loginUrl, payLoad)
            .then(response => {
                this.setState({ loading: false });
                if (response.status == 200 && response.data.loginInfo.roleId == 1) {
                    sessionStorage.setItem('loginInfo', JSON.stringify(response.data));
                    this.props.history.push('/admindashboard');
                    notify.show(response.data.message, "success");
                } else if (response.status == 200 && response.data.loginInfo.roleId == 2) {
                    sessionStorage.setItem('loginInfo', JSON.stringify(response.data));
                    this.setState({ isOtp: true })
                }
                else if (response.status == 206) {
                    notify.show(response.data.message, "error");
                }
                else if (response.data.message == "Session Expired") {
                    notify.show(response.data.message, "error");
                }
            })
            .catch(function (error) {
                console.log(error);
            });
    }
    verifyOtp(event) {
        event.preventDefault();
        let loginInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
        let payLoad = {
            'emailId': loginInfo.loginInfo.emailId,
            'securityKey': this.state.otp
        }
        this.setState({ loading: true });
        const otpVerifyUrl = API_BASE_URL + "login/secure";
        axios.post(otpVerifyUrl, payLoad)
            .then(response => {
                this.setState({ loading: false });
                if (response.status == 200) {
                    this.setState({ isOtp: false });
                    sessionStorage.setItem('loginInfo', JSON.stringify(response.data.loginInfo));
                    if (response.data.loginInfo.roleId === 2 && (response.data.loginInfo.kycStatus == 1 || response.data.loginInfo.kycStatus == 2)) {
                        this.props.history.push('/userDashboard');
                        notify.show(response.data.message, "success");
                    }
                    if (response.data.loginInfo.roleId === 2 && response.data.loginInfo.kycStatus == 0) {
                        this.props.history.push('/kycdetails');
                        notify.show(response.data.message, "success");
                    }
                }
                else if (response.status == 206) {
                    notify.show(response.data.message, "error");
                }
            })
            .catch(function (error) {
                console.log(error);
            });
    }
    pophandleSubmit(event) {
        event.preventDefault();
        let loginInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
        let payload = {
            "mediaId": loginInfo.googleId || loginInfo.userID,
            "userName": loginInfo.name,
            "emailId": this.state.emailId,
            "etherWalletPassword": this.state.etherWalletPassword,
            "confirmEtherWalletPassword": this.state.confirmEtherWalletPassword,
        }
        this.setState({ loading: true });
        const apiBaseUrl = API_BASE_URL + "social/media/login";
        axios.post(apiBaseUrl, payload)
            .then(response => {
                this.setState({ isSocialLoginPop: false })
                this.setState({ emailId: '', etherWalletPassword: '', confirmEtherWalletPassword: '' })
                this.setState({ loading: false });
                if (response.status == 200) {
                    sessionStorage.setItem('loginInfo', JSON.stringify(response.data.loginInfo));
                    if (response.data.loginInfo.roleId === 3 && response.data.loginInfo.kycStatus == 1) {
                        this.props.history.push('/userDashboard');
                        notify.show(response.data.message, "success");
                    }
                    if (response.data.loginInfo.roleId === 3 && response.data.loginInfo.kycStatus == 0) {
                        this.props.history.push('/kycdetails');
                        notify.show(response.data.message, "success");
                    }
                } else if (response.status == 206) {
                    notify.show(response.data.message, "error");
                    if (response.data.message == "Session Expired") {
                        this.props.history.push('/login')
                        notify.show(response.data.message, "error");
                    }
                }
            })
    }
    render() {
        return (
            // < !--- Login-- >
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
                    <div className="social-media">
                        <h1>Sign up with your favourite social profile</h1>
                        <ul>
                            <li>
                                <GoogleLogin
                                    clientId={'1019009733807-inp722busckp7dpku718fklgp5gghr4c.apps.googleusercontent.com'}
                                    onSuccess={this.googleSignIn}
                                    onFailure={this.googleSignIn}
                                    buttonText=""
                                    className='fa fa-google-plus googleplusicon' />
                            </li>
                            <li>
                                <FacebookLogin
                                    cssClass="loginBtn loginBtn--facebook fa fa-facebook facebookicon"
                                    appId="242636749660410"
                                    textButton=""
                                    autoLoad={false}
                                    fields="name,email,picture"
                                    callback={this.responseFacebook}
                                />
                            </li>
                        </ul>
                    </div>
                </div>
                <div className="aleef-signin-right">
                    <div className="aleef-container">
                        <div className="aleef-signin-form">
                            <h1 className="text-center">Log in</h1>
                            <form onSubmit={this.loginAleef}>
                                <div className="form-group">
                                    <label>
                                        Email
                            </label>
                                    <input type="text" placeholder="info@gmail.com" name='emailId' value={this.state.emailId} onChange={this.handleChange} />
                                    <div style={{ color: 'yellow' }}>{this.state.errors.emailId}</div>
                                </div>
                                <div className="form-group">
                                    <label>
                                        Password
                            </label>
                                    <input type="password" placeholder=".........." name='password' value={this.state.password} onChange={this.handleChange} />
                                    <div style={{ color: 'yellow' }}>{this.state.errors.password}</div>
                                </div>

                                <div className="form-group text-center">
                                    <button type="submit" className="aleef-signin-btn" disabled={!this.state.formValid}>Log in</button>
                                </div>
                                <div className="forgot-link">

                                    <NavLink to='/forgetpassword'>Forgot Password ?</NavLink>
                                    <p>Don’t have an Account ?
                                <NavLink to='/register'> Register Now</NavLink>
                                    </p>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
                {this.state.isOtp && <div className='loaderBg loaderimg'>
                    <div className="modal fade in" role="dialog" aria-hidden="true" style={{ display: "block" }}>
                        <div className="modal-dialog dialogMod">
                            {/* <!-- Modal content--> */}
                            <div className="modal-content">
                                <button type="button" className="closeBg" onClick={() => {
                                    this.setState({ isOtp: false });
                                }}>&#x2716;</button>
                                <div className="modal-body bodyMod">
                                    <img src="src/public/image/otp.png" />
                                    <h1>Please Enter Your Secret Pin</h1>
                                    <form onSubmit={this.verifyOtp} >
                                        <input type="password" name="otp" value={this.state.otp} placeholder="Enter your Secret pin" onChange={this.handleChange} />
                                        <div className="forgotpwd">
                                            <NavLink to='/forgetsecretpin'>Forgot Secret Pin ?</NavLink>
                                        </div>
                                        <button type="submit" className="btn-verfy" >Verify</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>}
                {this.state.isSocialLoginPop && <div className='loaderBg'>
                    <div className='loaderimg'>
                        <div className="reset-passwd-div1">
                            <div className="reset-passwd-section1">
                                <div className="reset-passwd-head">
                                    <button type="button" class="close" onClick={() => {
                                        this.setState({ isSocialLoginPop: false });
                                    }}>×</button>
                                    <h4 class="modal-title">Register</h4>
                                </div>
                                <div className="reset-passwd-cont">
                                    <form name="deleteTokens">
                                        <div className="form-group">

                                            <input type="text" className="form-control"
                                                placeholder="Email Id" name="emailId" value={this.state.emailId} onChange={this.handleChangePop} />
                                            <div style={{ color: 'red' }}>{this.state.errorsPop.emailId}</div>
                                        </div>
                                        <div className="form-group">

                                            <input type="password" className="form-control"
                                                placeholder="Wallet Password" name="etherWalletPassword" value={this.state.etherWalletPassword} onChange={this.handleChangePop} />
                                            <div style={{ color: 'red' }}>{this.state.errorsPop.etherWalletPassword}</div>
                                        </div>
                                        <div className="form-group">

                                            <input type="password" className="form-control"
                                                placeholder="Confirm Wallet Password" name="confirmEtherWalletPassword" value={this.state.confirmEtherWalletPassword} onChange={this.handleChangePop} />
                                            <div style={{ color: 'red' }}>{this.state.errorsPop.confirmEtherWalletPassword}</div>
                                        </div>
                                        <div className="form-group text-center">
                                            <button type="button" className="btn-resent" onClick={this.pophandleSubmit}>OK</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>}
            </div>
        )
    }
}

export default Login;