import React from 'react';
import validator from 'validator';
import axios from 'axios';
import { notify } from 'react-notify-toast';
import { ScaleLoader } from 'react-spinners';
import IntlTelInput from 'react-intl-tel-input';
import 'react-intl-tel-input/dist/libphonenumber.js';
import 'react-intl-tel-input/dist/main.css';

import { API_BASE_URL } from '../public/constants/ApiUrl';

class Contactus extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            description: '',
            mobileNo1: '',
            emailId1: '',
            userName1: '',
            subject: '',
            errors1: { userName1: '', emailId1: '', mobileNo1: '', description: '', subject: '' },
            userNameValid1: false,
            emailIdValid1: false,
            mobileNoValid1: false,
            descriptionValid: false,
            subjectValid: false,
            formValid1: false,
            loading: false,
            preRegisterMsg: ''
        }
        this.handleChange = this.handleChange.bind(this);
        this.sendContactDetails = this.sendContactDetails.bind(this);
        this.mobileNoHandler = this.mobileNoHandler.bind(this);
    }

    mobileNoHandler(status, value, countryData, number, id) {
        this.setState({
            mobileNo1: number,
            mobileNoValid: status
        });
        if (status == false) {
            this.state.errors1.mobileNo1 = 'Please enter valid phone number';
        }
        else if (status == true) {
            this.state.errors1.mobileNo1 = '';
        }
    }

    handleChange(e) {
        const value = e.target.value;
        const name = e.target.name;
        this.setState({ [name]: value },
            () => { this.validateField(name, value) });
    }

    validateField(fieldName, value) {
        let fieldValidationErrors1 = this.state.errors1;
        let userNameValid1 = this.state.userNameValid1;
        let emailIdValid1 = this.state.emailIdValid1;
        let subjectValid = this.state.subjectValid;
        let descriptionValid = this.state.descriptionValid;
        switch (fieldName) {
            case 'userName1':
                userNameValid1 = !(value.length < 6 || value.length > 20);
                fieldValidationErrors1.userName1 = userNameValid1 ? '' : 'Username should be 6 to 20 characters';
                break;
            case 'emailId1':
                emailIdValid1 = value.match(/^([\w.%+-]+)@([\w-]+\.)+([\w]{2,})$/i);
                fieldValidationErrors1.emailId1 = emailIdValid1 ? '' : ' Please enter valid email id';
                break;
            case 'subject':
                subjectValid = !(value.length <= 0 || value.length > 25);
                fieldValidationErrors1.subject = subjectValid ? '' : 'Subject should not empty';
                break;
            case 'description':
                descriptionValid = !(value.length <= 0 || value.length > 200);
                fieldValidationErrors1.description = descriptionValid ? '' : 'Description should not empty';
                break;
            default:
                break;
        }

        this.setState({
            errors1: fieldValidationErrors1,
            userNameValid1: userNameValid1,
            emailIdValid1: emailIdValid1,
            subjectValid: subjectValid,
            descriptionValid: descriptionValid
        }, this.validateForm);
    }

    validateForm() {
        this.setState({
            formValid1: this.state.emailIdValid1 && this.state.userNameValid1 &&
                this.state.subjectValid && this.state.descriptionValid
        })
    }

    sendContactDetails() {
        let payload = {
            'userName': this.state.userName1,
            'emailId': this.state.emailId1,
            'mobileNo': this.state.mobileNo1,
            'subject': this.state.subject,
            'description': this.state.description
        }
        let contactus = API_BASE_URL + 'contact/us';
        this.setState({ loading: true })
        axios.post(contactus, payload)
            .then(res => {
                this.setState({ preRegisterMsg: res.data.status })
                this.setState({ userName1: '', emailId1: '', mobileNo1: '', description: '', subject: '' });
                this.setState({ formValid1: false })
                this.setState({ loading: false })
                notify.show(res.data.message, "success");
            })
            .catch(function (error) {
                // console.log(error)
            })
    }

    render() {
        return (
            <div className="col-md-9 contact_det">
                <form>
                    {this.state.loading && <div className='loaderBg'>
                        <div className='loaderimg'>
                            <ScaleLoader
                                color={'#fff'}
                                loading={this.state.loading}
                            />
                        </div>
                    </div>}
                    <div className="form-group col-md-12">
                        <input type="text" name='userName1' value={this.state.userName1} onChange={this.handleChange} className="form-control" placeholder="Name" />
                        <div style={{ color: "red" }}>{this.state.errors1.userName1}</div>
                    </div>
                    <div className="form-group col-md-12">
                        <input type="text" name='emailId1' value={this.state.emailId1} onChange={this.handleChange} className="form-control" placeholder="Email Address" />
                        <div style={{ color: "red" }}>{this.state.errors1.emailId1}</div>
                    </div>
                    <div className="col-md-12 form-group">
                        <IntlTelInput
                            name='mobileNo1'
                            value={this.state.mobileNo1}
                            onChange={this.handleChange}
                            onPhoneNumberChange={this.mobileNoHandler}
                            onPhoneNumberBlur={this.mobileNoHandler}
                            css={['intl-tel-input', 'form-control']}
                            utilsScript={'libphonenumber.js'}
                        />
                        <div style={{ color: "red" }}>{this.state.errors1.mobileNo1}</div>
                    </div>
                    <div className="form-group col-md-12">
                        <input type="text" name='subject' value={this.state.subject} onChange={this.handleChange} className="form-control" placeholder="Subject" />
                        <div style={{ color: "red" }}>{this.state.errors1.subject}</div>
                    </div>
                    <div className="form-group col-md-12">
                        <textarea className="form-control" name='description' value={this.state.description} onChange={this.handleChange} rows="5" placeholder="Description"></textarea>
                        <div style={{ color: "red" }}>{this.state.errors1.description}</div>
                    </div>
                    <div className="col-md-12">
                        <button type="button" className="btn btn-default send_msg" disabled={!this.state.formValid1} onClick={this.sendContactDetails}>SEND MESSAGE</button>
                    </div>
                </form>
            </div>
        )
    }
}
export default Contactus;