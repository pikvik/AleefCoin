import React from 'react';
import AleefBoard from '../Common/aleefBoard';
import { NavLink } from 'react-router-dom';
import axios from 'axios';
import Notifications, { notify } from 'react-notify-toast';
import validator from 'validator';
import { API_BASE_URL } from '../Common/apiUrl';
import { ScaleLoader } from 'react-spinners';
import IntlTelInput from 'react-intl-tel-input';
import 'react-intl-tel-input/dist/libphonenumber.js';
import 'react-intl-tel-input/dist/main.css';
import { CountryDropdown, RegionDropdown } from 'react-country-region-selector';
import ToggleMenu from '../Common/togglemenu';

class KycDetails extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      fullName: '',
      emailId: '',
      mobileNo: '',
      file1: "",
      dob: '',
      homeAddress: '',
      city: '',
      file2: "",
      gender: '',
      errors: { fullName: '', emailId: '', mobileNo: '', file1: '', dob: '', homeAddress: '', city: '', file2: '', gender: '', oldPassword: '', password: '', confirmPassword: '' },
      fullNameValid: false,
      emailIdValid: false,
      mobileNoValid: false,
      file1Valid: false,
      dobValid: false,
      homeAddressValid: false,
      cityValid: false,
      file2Valid: false,
      genderValid: false,
      formValid: false,
      oldPasswordValid: false,
      passwordValid: false,
      confirmPasswordValid: false,
      sessionInfo: [],
      secretPin: '',
      country: '',
      region: '',
      kycStat: '',
      doc1Name: '',
      doc2Name: '',
      errorcity: '',
      errorregion: ''
    }
    let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
    if (sessionStorage.getItem('loginInfo') == null) {
      props.history.push('/login');
    }
    this.handleChange = this.handleChange.bind(this);
    this.handleChange1 = this.handleChange1.bind(this);
    this.handleChangeFile = this.handleChangeFile.bind(this);
    this.uploadKycDetails = this.uploadKycDetails.bind(this);
    this.logOut = this.logOut.bind(this);
    this.mobileNoHandler = this.mobileNoHandler.bind(this);
    this.toggleResetPwd = this.toggleResetPwd.bind(this);
    this.handleCheck = this.handleCheck.bind(this);
    this.selectCountry = this.selectCountry.bind(this);
    this.selectRegion = this.selectRegion.bind(this);
    this.myProfileView = this.myProfileView.bind(this);
  }
  componentDidMount() {
    let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
    this.setState({ sessionInfo })
    if (JSON.parse(sessionStorage.getItem('kycInfo')) != null) {
      let kycInfo = JSON.parse(sessionStorage.getItem('kycInfo'));
      this.myProfileView(kycInfo);
    }
    else {
      let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
      const payload = {
        sessionId: sessionInfo.sessionId
      }
      this.setState({ loading: true });
      const getKyc = API_BASE_URL + "view/user/kyc";
      axios.post(getKyc, payload)
        .then(response => {
          this.setState({ loading: false });
          if (response.status == 200) {
            this.myProfileView(response.data.kycInfo);
          } else if (response.status == 206) {
            notify.show(response.data.message, "error");
          }
          else if (response.data.message == 'Session Expired') {
            this.props.history.push('/login');
            notify.show(response.data.message, "error");
          }
        })
        .catch(function (error) {
          // console.log(error);
        });
    }
  }
  selectCountry(val) {
    if (val == '') {
      this.setState({ errorcity: 'please select country' })
    }
    else {
      this.setState({ errorcity: '' })
    }
    this.setState({ country: val });
    this.setState({ errorregion: 'please select state' });
  }

  selectRegion(val) {
    if (val == '') {
      this.setState({ errorregion: 'please select state' });
    }
    else {
      this.setState({ errorregion: '' });
    }
    this.setState({ region: val });
  }

  myProfileView(response) {
    this.setState({
      fullName: response.fullName, emailId: response.emailId,
      homeAddress: response.address, region: response.city, country: response.country,
      dob: response.dob, gender: response.gender, kycStat: response.kycStatus,
      mobileNo: response.mobileNo, doc1Name: response.kycDoc1Name, doc2Name: response.kycDoc2Name
    });
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
        // console.log(error);
      });
    this.setState({ oldPassword: '', password: '', confirmPassword: '', secretPin: '' })
  }
  handleChange1(e) {
    const value = e.target.value;
    const name = e.target.name;
    this.setState({ [name]: value },
      () => { this.validateField1(name, value) });
  }
  validateField1(fieldName, value) {
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
  handleChange(e) {
    const value = e.target.value;
    const name = e.target.name;
    this.setState({ [name]: value },
      () => { this.validateField(name, value) });
  }
  validateField(fieldName, value) {
    let fieldValidationErrors = this.state.errors;
    let fullNameValid = this.state.fullNameValid;
    let emailIdValid = this.state.emailIdValid;
    let file1Valid = this.state.file1Valid;
    let dobValid = this.state.dobValid;
    let homeAddressValid = this.state.homeAddressValid;
    let cityValid = this.state.cityValid;
    let file2Valid = this.state.file2Valid;

    switch (fieldName) {
      case 'fullName':
        fullNameValid = validator.isAlpha(value) || validator.contains(value, " ");
        fieldValidationErrors.fullName = fullNameValid ? '' : 'Fullname should be alphabetic';
        break;
      case 'emailId':
        emailIdValid = value.match(/^([\w.%+-]+)@([\w-]+\.)+([\w]{2,})$/i);
        fieldValidationErrors.emailId = emailIdValid ? '' : 'Please enter valid Email Id';
        break;
      case 'homeAddress':
        homeAddressValid = (validator.isAlphanumeric(value) || validator.contains(value, " "));
        fieldValidationErrors.homeAddress = homeAddressValid ? '' : 'Please Enter valid Address';
        break;
      case 'city':
        cityValid = validator.isAlpha(value) || validator.contains(value, " ");
        fieldValidationErrors.city = cityValid ? '' : 'Please enter valid city name';
        break;
      default:
        break;
    }

    this.setState({
      errors: fieldValidationErrors,
      fullNameValid: fullNameValid,
      emailIdValid: emailIdValid,
      file1Valid: file1Valid,
      dobValid: dobValid,
      homeAddressValid: homeAddressValid,
      cityValid: cityValid,
      file2Valid: file2Valid
    }, this.validateForm);
  }
  validateForm() {
    this.setState({
      formValid: this.state.fullNameValid && this.state.emailIdValid
        && this.state.file1Valid && this.state.dobValid && this.state.homeAddressValid && this.state.cityValid
        && this.state.file2Valid
    })
  }

  uploadKycDetails(event) {
    event.preventDefault();
    let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
    const details = {
      "sessionId": sessionInfo.sessionId, "fullName": this.state.fullName,
      "dob": this.state.dob, "address": this.state.homeAddress, "gender": this.state.gender,
      "emailId": sessionInfo.emailId || this.state.emailId, "city": this.state.region, "country": this.state.country,
      "mobileNo": this.state.mobileNo
    }
    const formData = new FormData();
    formData.append('userInfo', JSON.stringify(details));
    formData.append('kycDoc1', this.state.file1);
    formData.append('kycDoc2', this.state.file2);
    const uploadKycUrl = API_BASE_URL + "uploadkyc";
    this.setState({ loading: true });
    axios.post(uploadKycUrl,
      formData,
      {
        'headers': {
          'content-type': 'multipart/form-data'
        }
      })
      .then(response => {
        this.setState({ loading: false });
        if (response.status == 200) {
          this.myProfileView(response.data.kycInfo);
          sessionStorage.setItem('kycInfo', JSON.stringify(response.data.kycInfo));
          this.props.history.push('/userdashboard');
          notify.show(response.data.message, "success");
        } else if (response.status == 206) {
          notify.show(response.data.message, "error");
        } else if (response.data.message == "Session Expired") {
          sessionStorage.removeItem('loginInfo');
          this.props.history.push('/login')
          notify.show(response.data.message, "error");
        }
      })
      .catch(function (error) {
        // console.log(error);
      });
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

  handleChangeFile(event) {
    event.preventDefault();
    this.setState({ [event.target.name]: event.target.files[0] });
  }
  handleCheck(e) {
    this.setState({ gender: e.target.value });
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
          sessionStorage.clear();
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
        // console.log(error);
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
                {this.state.kycStat == 1 || this.state.kycStat == 2 || this.state.sessionInfo.kycStatus == 1 ||
                  this.state.sessionInfo.kycStatus == 2 ?
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
                  </ul> : <ul className="sidebar-menu" id="nav-accordion">
                    <li className="nav-profile logo-nav"></li>
                    <li>
                      <NavLink to={'/kycdetails'}>
                        <img src="src/public/image/transaction.png" />
                        <span className="m_left">Manage KYC</span>
                      </NavLink>
                    </li>
                  </ul>}
              </div>
            </div>
          </aside>
          <section id="main-content">
            <section className="wrapper">
              <div className="dashboard-title title-inline">
                <h1>KYC Details</h1>
              </div>
              <section className="admin-content">
                <div className="col-md-12 kyc-detail">
                  <form onSubmit={this.uploadKycDetails}>
                    <div className="col-md-6 col-sm-6 col-xs-12">
                      <div className="form-group">
                        <label className="control-label col-sm-5" htmlFor="email">Full Name <sup>*</sup></label>
                        <div className="col-sm-6">
                          <input type="text" className="form-control" onChange={this.handleChange} value={this.state.fullName || ''} name="fullName" required />
                          <div style={{ color: "red" }}>{this.state.errors.fullName}</div>
                        </div>
                      </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-12">
                      <div className="form-group">
                        <label className="control-label col-sm-5" htmlFor="email">Date Of Birth <sup>*</sup></label>
                        <div className="col-sm-6">
                          <input type="date" className="form-control" max={"9999-12-31"} onChange={this.handleChange} value={this.state.dob || ''} name="dob" required />
                          <div style={{ color: "red" }}>{this.state.errors.dob}</div>
                        </div>
                      </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-12">
                      <div className="form-group">
                        <label className="control-label col-sm-5" htmlFor="email">Gender <sup>*</sup></label>
                        <div className="col-sm-6 selct">
                          <select value={this.state.gender} onChange={this.handleCheck} required>
                            <option value="">--SelectGender--</option>
                            <option value="male"> Male</option>
                            <option value="female">Female</option>
                            <option value="Transgender">Transgender</option>
                          </select>
                          <div style={{ color: "red" }}>{this.state.errors.gender}</div>
                        </div>
                      </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-12">
                      <div className="form-group">
                        <label className="control-label col-sm-5" htmlFor="email"> Address/City <sup>*</sup></label>
                        <div className="col-sm-6">
                          <input type="text" className="form-control" onChange={this.handleChange} value={this.state.homeAddress || ''} name="homeAddress" required />
                          <div style={{ color: "red" }}>{this.state.errors.homeAddress}</div>
                        </div>
                      </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-12">
                      <div className="form-group">
                        <label className="control-label col-sm-5" htmlFor="email">Country <sup>*</sup></label>
                        <div className="col-sm-6 selct">
                          <CountryDropdown
                            value={this.state.country}
                            onChange={(val) => this.selectCountry(val)} required />
                          <div style={{ color: "red" }}>{this.state.errors.city || this.state.errorcity}</div>
                        </div>
                      </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-12">
                      <div className="form-group">
                        <label className="control-label col-sm-5" htmlFor="email">State <sup>*</sup></label>
                        <div className="col-sm-6 selct">
                          <RegionDropdown
                            country={this.state.country}
                            value={this.state.region}
                            onChange={(val) => this.selectRegion(val)} required />
                        </div>
                        <div style={{ color: "red" }}>{this.state.errors.country || this.state.errorregion}</div>
                      </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-12">
                      <div className="form-group">
                        <label className="control-label col-sm-5" htmlFor="email">Mobile No <sup>*</sup></label>
                        <div className="col-sm-6">
                          <IntlTelInput
                            name='mobileNo'
                            value={this.state.mobileNo}
                            onChange={this.handleChange}
                            onPhoneNumberChange={this.mobileNoHandler}
                            onPhoneNumberBlur={this.mobileNoHandler}
                            css={['intl-tel-input', 'form-control']}
                            utilsScript={'libphonenumber.js'}
                            required
                          />
                          <div style={{ color: "red" }}>{this.state.errors.mobileNo}</div>
                        </div>
                      </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-12">
                      <div className="form-group">
                        <label className="control-label col-sm-5" htmlFor="email">Email Id <sup>*</sup></label>
                        <div className="col-sm-6">
                          <input type="email" className="form-control" onChange={this.handleChange} value={this.state.sessionInfo.emailId || this.state.emailId || ''} name="emailId" readOnly />
                          <div style={{ color: "red" }}>{this.state.errors.emailId}</div>
                        </div>
                      </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-12">
                      <div className="form-group">
                        <label className="control-label col-sm-5" htmlFor="email"> Photo ID <sup>*</sup></label>
                        <div className="col-sm-6">
                          <input type="file" className="form-control" onChange={this.handleChangeFile} ref='file' name="file1" required />
                        </div>
                      </div>
                      <div className="form-group">
                        <label className="control-label col-sm-5" htmlFor="email"> Photo ID File Name</label>
                        <div className="col-sm-6">
                          <span> {this.state.doc1Name}</span>
                        </div>
                      </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-12">
                      <div className="form-group">
                        <label className="control-label col-sm-5" htmlFor="email"> Residential ID</label>
                        <div className="col-sm-6">
                          <input type="file" className="form-control" onChange={this.handleChangeFile} ref='file1' name="file2" />
                        </div>
                      </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-12">
                      <div className="form-group">
                        <label className="control-label col-sm-5" htmlFor="email"> Residential ID File Name</label>
                        <div className="col-sm-6">
                          <span> {this.state.doc2Name}</span>
                        </div>
                      </div>
                    </div>
                    <div className="btnBg">
                      <button type="submit" className="kycSubmit kycSubBtn" disabled={this.state.errorcity != '' || this.state.errorregion != '' || this.state.fullName == "" || this.state.dob == "" || this.state.gender == "" || this.state.homeAddress == "" || this.state.country == '' || this.state.region == '' || this.state.mobileNo == ""}>Submit</button>
                    </div>
                  </form>
                </div>
              </section>
            </section>
          </section>
        </section>
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
                        <input type="password" className="form-control" placeholder="Old-Password" name="oldPassword" value={this.state.oldPassword} onChange={this.handleChange1} />
                        <div style={{ color: 'red' }}>{this.state.errors.oldPassword}</div>
                      </div>
                      <div className="form-group">
                        <input type="password" className="form-control" placeholder="New-Password" name="password" value={this.state.password} onChange={this.handleChange1} />
                        <div style={{ color: 'red' }}>{this.state.errors.password}</div>
                      </div>
                      <div className="form-group">
                        <input type="password" className="form-control" placeholder="Confirm-password" name="confirmPassword" value={this.state.confirmPassword} onChange={this.handleChange1} />
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
      </div >
    )
  }
}
export default KycDetails;