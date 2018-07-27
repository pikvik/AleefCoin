import React from 'react';
import Notifications, { notify } from 'react-notify-toast';
import validator from 'validator';
import axios from 'axios';
import { NavLink } from 'react-router-dom';
import { ScaleLoader } from 'react-spinners';
import { API_BASE_URL } from '../Common/apiUrl';
import queryString from 'query-string';
import IntlTelInput from 'react-intl-tel-input';
import 'react-intl-tel-input/dist/libphonenumber.js';
import 'react-intl-tel-input/dist/main.css';

class Register extends React.Component {
    constructor(props) {
        super(props);
        const parsed = queryString.parse(props.location.search);
        this.state = {
            sponser_id: parsed.referenceId,
            userName: "",
            emailId: "",
            mobileNo: "",
            password: "",
            confirmPassword: "",
            etherWalletPassword: "",
            confirmEtherWalletPassword: "",
            errors: { userName: '', emailId: '', mobileNo: '', password: '', confirmPassword: '', etherWalletPassword: '', confirmEtherWalletPassword: '' },
            userNameValid: false,
            emailIdValid: false,
            mobileNoValid: false,
            passwordValid: false,
            confirmPasswordValid: false,
            etherWalletPasswordValid: false,
            confirmEtherWalletPasswordValid: false,
            formValid: false,
            loading: false,
            isChecked: false,
            Checked: '',
            type: 'password',
            type1: 'password'
        }
        this.showHide = this.showHide.bind(this);
        this.showHide1 = this.showHide1.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.registerAleef = this.registerAleef.bind(this);
        this.validateForm = this.validateForm.bind(this);
        this.mobileNoHandler = this.mobileNoHandler.bind(this);
        this.checkChange = this.checkChange.bind(this);
    }

    mobileNoHandler(status, value, countryData, number, id) {
        this.setState({
            mobileNo: number,
            mobileNoValid: status
        });
        if (status == false) {
            this.state.errors.mobileNo = 'Please enter valid phone number';
        }
        else if (status == true) {
            this.state.errors.mobileNo = '';
        }
    }

    showHide(e) {
        e.preventDefault();
        this.setState({
            type: this.state.type === 'input' ? 'password' : 'input'
        })
    }
    showHide1(e) {
        e.preventDefault();
        this.setState({
            type1: this.state.type1 === 'input' ? 'password' : 'input'
        })
    }
    checkChange() {
        this.setState({ isChecked: !this.state.isChecked })
        if (this.state.isChecked) {
            this.setState({ Checked: 'Please accept the terms and conditions' })
        }
        else {
            this.setState({ Checked: '' })
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
        let userNameValid = this.state.userNameValid;
        let emailIdValid = this.state.emailIdValid;
        let mobileNoValid = this.state.mobileNoValid;
        let passwordValid = this.state.passwordValid;
        let confirmPasswordValid = this.state.confirmPasswordValid;
        let etherWalletPasswordValid = this.state.etherWalletPasswordValid;
        let confirmEtherWalletPasswordValid = this.state.confirmEtherWalletPasswordValid;

        switch (fieldName) {
            case 'userName':
                userNameValid = !(value.length < 6 || value.length > 20);
                fieldValidationErrors.userName = userNameValid ? '' : 'Username should be 6 to 20 characters';
                break;
            case 'emailId':
                emailIdValid = value.match(/^([\w.%+-]+)@([\w-]+\.)+([\w]{2,})$/i);
                fieldValidationErrors.emailId = emailIdValid ? '' : ' Please enter valid email id';
                break;
            case 'mobileNo':
                mobileNoValid = validator.isNumeric(value) && validator.isLength(value, 10, 10);
                fieldValidationErrors.mobileNo = mobileNoValid ? '' : 'Please enter valid phone number';
                break;
            case 'password':
                passwordValid = value.length >= 8 && value.match(/^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\d]){1,})(?=(.*[\W]){1,})(?!.*\s).{8,}$/);
                fieldValidationErrors.password = passwordValid ? '' : 'Must contain atleast one uppercase, one lowercase ,one number, one special character and must contain minimum 8 character';
                break;
            case 'confirmPassword':
                confirmPasswordValid = validator.equals(value, this.state.password);
                fieldValidationErrors.confirmPassword = confirmPasswordValid ? '' : 'Password does not match';
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
            errors: fieldValidationErrors,
            userNameValid: userNameValid,
            emailIdValid: emailIdValid,
            mobileNoValid: mobileNoValid,
            passwordValid: passwordValid,
            confirmPasswordValid: confirmPasswordValid,
            etherWalletPasswordValid: etherWalletPasswordValid,
            confirmEtherWalletPasswordValid: confirmEtherWalletPasswordValid
        }, this.validateForm);
    }
    validateForm() {
        this.setState({
            formValid: this.state.userNameValid && this.state.emailIdValid && this.state.mobileNoValid
                && this.state.passwordValid && this.state.confirmPasswordValid && this.state.etherWalletPasswordValid
                && this.state.confirmEtherWalletPasswordValid
        })
    }
    registerAleef() {
        const payload = {
            "sponser_id": this.state.sponser_id,
            "userName": this.state.userName,
            "mobileNo": this.state.mobileNo,
            "emailId": this.state.emailId,
            "password": this.state.password,
            "confirmPassword": this.state.confirmPassword,
            "etherWalletPassword": this.state.etherWalletPassword,
            "confirmEtherWalletPassword": this.state.confirmEtherWalletPassword
        }
        this.setState({ loading: true });
        const registerUrl = API_BASE_URL + "register";
        axios.post(registerUrl, payload)
            .then(response => {
                this.setState({ loading: false });
                if (response.status == 200) {
                    this.props.history.push("/login");
                    notify.show(response.data.message, "success");
                } else if (response.status == 206) {
                    this.setState({ loading: false });
                    notify.show(response.data.message, "error");
                }
                else if (response.data.message == 'Session Expired') {
                    this.setState({ loading: false });
                    this.props.history.push('/login');
                    notify.show(response.data.message, "error");
                }
            })
            .catch(function (error) {
                console.log(error)
            });
        this.setState({
            userName: "", mobileNo: "", emailId: "", password: "", confirmPassword: ""
            , etherWalletPassword: "", confirmEtherWalletPassword: "", errors: {}
        });
    }

    render() {
        return (
            <div>
                <Notifications />
                {/* <!---Register--> */}
                <div className="aleef-signin signBg">
                    {this.state.loading && <div className='loaderBg'>
                        <div className='loaderimg'>
                            <ScaleLoader
                                size={180}
                                color={'#fff'}
                                loading={this.state.loading}
                            />
                        </div>
                    </div>}
                    <div className="aleef-signin-left">
                        <div className="aleef-logo">
                            <img src="src/public/image/main-logo.png" />
                        </div>
                    </div>
                    <div className="aleef-signin-right">
                        <div className="aleef-container">
                            <div className="aleef-signin-form">
                                <h1 className="text-center">Register</h1>
                                <form>
                                    <div className="form-group">
                                        <label>
                                            Name
                            </label>
                                        <input type="text" name="userName" placeholder="Enter user name" value={this.state.userName} onChange={this.handleChange} />
                                        <div style={{ color: "yellow" }}>{this.state.errors.userName}</div>
                                    </div>
                                    <div className="form-group">
                                        <label>
                                            Mobile Number</label>
                                        <IntlTelInput
                                            name='mobileNo'
                                            value={this.state.mobileNo}
                                            onChange={this.handleChange}
                                            onPhoneNumberChange={this.mobileNoHandler}
                                            onPhoneNumberBlur={this.mobileNoHandler}
                                            css={['intl-tel-input', 'form-control']}
                                            utilsScript={'libphonenumber.js'}
                                        />
                                        <div style={{ color: "yellow" }}>{this.state.errors.mobileNo}</div>
                                    </div>
                                    <div className="form-group">
                                        <label>
                                            Email
                            </label>
                                        <input type="text" placeholder="info@gmail.com" name='emailId' value={this.state.emailId} onChange={this.handleChange} />
                                        <div style={{ color: "yellow" }}>{this.state.errors.emailId}</div>
                                    </div>
                                    <div className="form-group">
                                        <label>
                                            Password
                            </label>
                                        <input type={this.state.type} placeholder="************" name='password' value={this.state.password} onChange={this.handleChange} />
                                        <div style={{ color: "yellow" }}>{this.state.errors.password}</div>
                                    </div>
                                    <div className="form-group posrelative">
                                        <label>
                                            Confirm Password
                            </label>
                                        <input type={this.state.type} placeholder="************" name='confirmPassword' value={this.state.confirmPassword} onChange={this.handleChange} />
                                        <span className="showhide" onClick={this.showHide}>{this.state.type === 'input' ? 'Hide' : 'Show'}</span>
                                        <div style={{ color: "yellow" }}>{this.state.errors.confirmPassword}</div>
                                    </div>
                                    <div className="form-group">
                                        <label>
                                            EtherWallet Password
                            </label>
                                        <input type={this.state.type1} placeholder="************" name='etherWalletPassword' value={this.state.etherWalletPassword} onChange={this.handleChange} />
                                        <div style={{ color: "yellow" }}>{this.state.errors.etherWalletPassword}</div>
                                    </div>
                                    <div className="form-group posrelative">
                                        <label>
                                            Confirm EtherWallet Password
                            </label>
                                        <input type={this.state.type1} name='confirmEtherWalletPassword' placeholder="************" value={this.state.confirmEtherWalletPassword} onChange={this.handleChange} />
                                        <span className="showhide" onClick={this.showHide1}>{this.state.type1 === 'input' ? 'Hide' : 'Show'}

                                        </span>
                                        <div style={{ color: "yellow" }}>{this.state.errors.confirmEtherWalletPassword}</div>
                                    </div>
                                    <div className="form-group">
                                        <div className="cntr">
                                            <input className="hidden-xs-up" id="cbx" type="checkbox" checked={this.state.isChecked} onChange={this.checkChange} required />
                                            <label className="lbl" htmlFor="cbx">I agree the
											<a href='https://www.aleefcoin.io/termsofservice' target='_blank' > Terms Of Service</a> and
                                                <a href='https://www.aleefcoin.io/privacypolicy' target='_blank'> Privacy Policy</a></label>
                                            {!this.state.isChecked && <div style={{ color: "yellow" }}>{this.state.Checked}</div>}
                                        </div>
                                    </div>
                                    <div className="form-group text-center">
                                        <button type="button" className="aleef-signin-btn" disabled={!this.state.formValid || !this.state.isChecked} onClick={this.registerAleef}>Register</button>
                                    </div>
                                    <div className="forgot-link">
                                        <p>Already have an account ?
                                <NavLink to={'/login'}> Sign In</NavLink>
                                        </p>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="success-message hide">
                    <div className="success-div">
                        <svg id="successAnimation" className="animated" xmlns="http://www.w3.org/2000/svg" width="130" height="130" viewBox="0 0 70 70">
                            <path id="successAnimationResult" fill="#D8D8D8" d="M35,60 C21.1928813,60 10,48.8071187 10,35 C10,21.1928813 21.1928813,10 35,10 C48.8071187,10 60,21.1928813 60,35 C60,48.8071187 48.8071187,60 35,60 Z M23.6332378,33.2260427 L22.3667622,34.7739573 L34.1433655,44.40936 L47.776114,27.6305926 L46.223886,26.3694074 L33.8566345,41.59064 L23.6332378,33.2260427 Z"
                            />
                            <circle id="successAnimationCircle" cx="35" cy="35" r="24" stroke="#979797" strokeWidth="2" strokeLinecap="round" fill="transparent"
                            />
                            <polyline id="successAnimationCheck" stroke="#979797" strokeWidth="2" points="23 34 34 43 47 27" fill="transparent" />
                        </svg>
                        <div className="success-content">
                            <p>Thanks for registering with us your account has been created. Please login with the credentials</p>

                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
export default Register;