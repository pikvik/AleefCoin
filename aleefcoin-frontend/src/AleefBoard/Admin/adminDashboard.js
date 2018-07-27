import React from 'react';
import { NavLink } from 'react-router-dom';
import { API_BASE_URL } from '../Common/apiUrl';
import { API_BASE_URL_VINAY } from '../Common/apiUrl';
import { ScaleLoader } from 'react-spinners';
import axios from 'axios';
import validator from 'validator';
import Notifications, { notify } from 'react-notify-toast';
import AleefBoard from '../Common/aleefBoard';
import ToggleMenu from '../Common/togglemenu';

class AdminDashboard extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      toAddress: "",
      amount: "",
      ethvalpwd: '',
      amount2: "",
      ethvalpwd2: '',
      oldPassword: "",
      password: "",
      confirmPassword: "",
      burntoken: '',
      sessionId: '',
      errors: { oldPassword: '', password: '', confirmPassword: '' },
      oldPasswordValid: false,
      passwordValid: false,
      confirmPasswordValid: false,
      loading: false,
      isBurn: false,
      sessionInfo: '',
      ethBalance: '',
      tokenBalance: '',
      tokenDetails: '',
      icoTo: false,
      showingIcotokens: [],
      secretPin: '',
      refBouns: {},
      isRefBouns: false
    }
    if (sessionStorage.getItem('loginInfo') == null) {
      props.history.push('/login');
    }
    if (sessionStorage.getItem('loginInfo') != null) {
      history.pushState(null, null, location.href);
      window.onpopstate = function (event) {
        history.go(1);
      };
    }
    this.logOut = this.logOut.bind(this);
    this.toggleResetPwd = this.toggleResetPwd.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.toggleValidate = this.toggleValidate.bind(this);
    this.toggleTransToken = this.toggleTransToken.bind(this);
    this.toggleBurnToken = this.toggleBurnToken.bind(this);
    this.getTokenDetails = this.getTokenDetails.bind(this);
    this.getTokenBalnce = this.getTokenBalnce.bind(this);
    this.getIcoDetails = this.getIcoDetails.bind(this);
    this.getRefferalBouns = this.getRefferalBouns.bind(this);

  }
  componentDidMount() {
    if (sessionStorage.getItem('loginInfo') != null) {
      let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
      this.getEtherBalnce(sessionInfo);
      this.getTokenBalnce(sessionInfo);
      this.getTokenDetails(sessionInfo);
      this.getRefferalBouns();
    }
  }
  getRefferalBouns() {
    if (sessionStorage.getItem('loginInfo') != null) {
      let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
      let payload = {
        'sessionId': sessionInfo.loginInfo.sessionId
      }
      this.setState({ loading: true })
      const apiBaseUrl = API_BASE_URL + "referral/admin";
      axios.post(apiBaseUrl, payload)
        .then(Response => {
          this.setState({ loading: false })
          if (Response.status == '200') {
            this.setState({ refBouns: Response.data.referralTokens })
          } else if (Response.status == 206) {
            if (Response.data.message == 'Session Expired') {
              this.props.history.push('/login');
              notify.show(Response.data.message, 'error')
            }
          }
        });
      this.setState({ emailId: '' })
    }
  }
  getIcoDetails() {
    const apiBaseUrl = API_BASE_URL + "get/icotoken/details";
    axios.get(apiBaseUrl)
      .then(response => {
        if (response.status === 200) {
          this.setState({
            showingIcotokens: response.data.icoTokensDetList
          });
        } else if (response.data.message === 'Session expired!') {
          sessionStorage.removeItem('userData');
          this.props.history.push('/login');

        }
      });
  }
  getTokenDetails(sessionId) {
    let payload = {
      'sessionId': sessionId.loginInfo.sessionId
    }
    const apiBaseUrl = API_BASE_URL + "admin/dashboard/details";
    axios.post(apiBaseUrl, payload)
      .then(response => {
        if (response.status === 200) {
          this.setState({ tokenDetails: response.data.adminDashboardDetails });
        } else if (response.data.message === 'Session expired!') {
          sessionStorage.removeItem('userData');
          this.props.history.push('/login');
          let myColor = { background: 'red', text: '#FFFFFF' };
          notify.show(response.data.message, 'custom', 5000, myColor);
        }
      })
  }
  getEtherBalnce(value) {
    this.setState({ sessionInfo: value.loginInfo });
    let token = {
      'sessionId': value.loginInfo.sessionId
    }
    const apiBaseUrl = API_BASE_URL + "ether/balance";

    axios.post(apiBaseUrl, token)
      .then(response => {
        if (response.status === 200) {
          this.setState({ ethBalance: response.data.etherBalanceInfo });
        } else if (response.data.message === 'Session expired!') {
          sessionStorage.removeItem('userData');
          this.props.history.push('/login');
          let myColor = { background: 'red', text: '#FFFFFF' };
          notify.show(response.data.message, 'custom', 5000, myColor);
        }
      })
  }
  getTokenBalnce(value) {
    let payload = {
      'sessionId': value.loginInfo.sessionId
    }
    const apiBaseUrl = API_BASE_URL + "token/balance/admin";
    axios.post(apiBaseUrl, payload)
      .then(response => {
        if (response.status === 200) {
          this.setState({ tokenBalance: response.data.tokenBalance });
        } else if (response.data.message === 'Session expired!') {
          sessionStorage.removeItem('loginInfo');
          this.props.history.push('/login');
          let myColor = { background: 'red', text: '#FFFFFF' };
          notify.show(response.data.message, 'custom', 5000, myColor);
        }
      })
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

  toggleValidate(event) {
    event.preventDefault();
    let errors = {}
    if (event.target.name == 'toAddress') {
      if (!validator.isAlphanumeric(event.target.value)) {
        errors.toAddress = "Please enter ether valid wallet address";
      }
    }
    if (event.target.name == 'ethvalpwd') {
      if (event.target.value.length < 8) {
        errors.ethvalpwd = "Please enter valid ether wallet password";
      }
    }
    if (event.target.name == 'amount') {
      if (!validator.isNumeric(event.target.value)) {
        errors.amount = "Please enter valid amount";
      }
    }
    if (event.target.name == 'ethvalpwd2') {
      if (event.target.value.length < 8) {
        errors.ethvalpwd2 = "Please enter valid ether wallet password";
      }
    }
    if (event.target.name == 'amount2') {
      if (!validator.isNumeric(event.target.value)) {
        errors.amount2 = "Please enter valid amount";
      }

    }
    this.setState({ [event.target.name]: event.target.value });
    this.setState({ errors });

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

  toggleTransToken() {
    let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
    const payload = {
      "sessionId": sessionInfo.loginInfo.sessionId,
      "toAddress": this.state.toAddress,
      "etherWalletPassword": this.state.ethvalpwd,
      "amount": this.state.amount
    }
    this.setState({ loading: true });
    const tokTransUrl = API_BASE_URL + "token/transfer";
    axios.post(tokTransUrl, payload)
      .then(response => {
        this.setState({ loading: false });
        if (response.status == 200) {
          notify.show(response.data.message, "success");
        }
        else if (response.status == 206) {
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
    this.setState({ toAddress: "", amount: "", ethvalpwd: '' })
  }

  toggleBurnToken() {
    this.setState({ isBurn: false });
    let sessionInfo = JSON.parse(sessionStorage.getItem('loginInfo'));
    let payload = {
      'sessionId': sessionInfo.loginInfo.sessionId,
      'etherWalletPassword': this.state.ethvalpwd2,
      'amount': this.state.amount2
    }
    this.setState({ loading: true });
    const burnUrl = API_BASE_URL + "token/burn";
    axios.post(burnUrl, payload)
      .then(response => {
        this.setState({ loading: false });
        if (response.status == 200) {
          notify.show(response.data.message, "success");
        }
        else if (response.status == 206) {
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
    this.setState({ amount2: "", ethvalpwd2: '' })
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
                  <a data-toggle="dropdown" className="dropdown-toggle">
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
              <div className="dashboard-title">
                <h1>Admin
            <span> Dashboard</span>
                </h1>
              </div>
              <div className="col-md-12 col-xs-12 col-sm-12">
                <div className="aleef-wallet-id">
                  <h1>Wallet Address :</h1>
                  <p>{this.state.sessionInfo.etherWalletAddress}</p>
                </div>
              </div>
              <div className="admin-list-box">
                <div className="container-fluid">
                  <div className="row">
                    <div className="col-lg-3 col-sm-6">
                      <div className="list-box-4">
                        <div className="widget-panel bg-red">
                          <div className="bg-img">
                            <img src="src/public/image/toal-icon.png" />
                          </div>
                          <div className="details_Cont">
                            <h2 className="counter">{this.state.tokenDetails.totalTokens}</h2>
                            <div className="visit-list">Total Tokens</div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className="col-lg-3 col-sm-6" onClick={() => {
                      this.getIcoDetails();
                      this.setState({ icoTo: true });
                    }}>
                      <div className="list-box-4">
                        <div className="widget-panel bg-success">
                          <div className="bg-img cursorpoint">
                            <img src="src/public/image/diamond.png" />
                          </div>
                          <div className="details_Cont">
                            <h2 className="counter">{this.state.tokenDetails.icoTokens}</h2>
                            <div className="visit-list">ICO Tokens</div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className="col-lg-3 col-sm-6">
                      <div className="list-box-4">
                        <div className="widget-panel bg-dpink">
                          <div className="bg-img">
                            <img src="src/public/image/diamond.png" />
                          </div>
                          <div className="details_Cont">
                            <h2 className="counter">{this.state.tokenDetails.soldTokens}</h2>
                            <div className="visit-list">Sold Tokens</div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className="col-lg-3 col-sm-6">
                      <div className="list-box-4">
                        <div className="widget-panel bg-blue">
                          <div className="bg-img">
                            <img src="src/public/image/bal-token.png" />
                          </div>
                          <div className="details_Cont">
                            <h2 className="counter">{this.state.tokenBalance.tokenAmount}</h2>
                            <div className="visit-list">Balance Tokens</div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className="col-lg-3 col-sm-6" onClick={() => {
                      this.setState({ isRefBouns: true })
                    }}>
                      <div className="list-box-4">
                        <div className="widget-panel bg-orange ">
                          <div className="bg-img cursorpoint">
                            <img src="src/public/image/token-bal.png" width="55" />
                          </div>
                          <div className="details_Cont">
                            <h2 className="counter">{this.state.refBouns.referralTokens}</h2>
                            <div className="visit-list">Referal Bonus</div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className="col-lg-3 col-sm-6" onClick={() => {
                      this.setState({ isBurn: true });
                    }}>
                      <div className="list-box-4">
                        <div className="widget-panel bg-orange ">
                          <div className="bg-img cursorpoint">
                            <img src="src/public/image/bitcoin-bal.png" />
                          </div>
                          <div className="details_Cont">
                            <h2 className="counter">{this.state.tokenDetails.burnTokens}</h2>
                            <div className="visit-list">Burn Unsold Tokens</div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className="col-lg-3 col-sm-6">
                      <div className="list-box-4">
                        <div className="widget-panel bg-purple">
                          <div className="bg-img">
                            <img src="src/public/image/ether.png" />
                          </div>
                          <div className="details_Cont">
                            <h2 className="counter">{this.state.ethBalance.etherBalance}</h2>
                            <div className="visit-list">Ether Balance</div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div className="col-lg-3 col-sm-6">
                      <div className="list-box-4">
                        <div className="widget-panel bg-pink">
                          <div className="bg-img">
                            <img src="src/public/image/user-2.png" />
                          </div>
                          <div className="details_Cont">
                            <h2 className="counter">{this.state.sessionInfo.usersCount}</h2>
                            <div className="visit-list">Users</div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div className="coin-transfer">
                <div className="container-fluid">
                  <div className="col-md-12 col-sm-12 col-xs-12 coin-transfer-padd">
                    <div className="transaction-title">
                      <h1>Coin
                  <span> Transfer</span>
                      </h1>
                    </div>
                    <div className="coin-transfer-wrap">
                      <form className="adjust_padding">
                        <div className="form-group">
                          <label htmlFor="wallet"> Wallet Address</label>
                          <input type="text" className="form-control" placeholder="Enter wallet address" name="toAddress" value={this.state.toAddress || ''} onChange={this.toggleValidate} />
                          <div style={{ color: 'yellow' }}>{this.state.errors.toAddress}</div>
                        </div>
                        <div className="form-group">
                          <label htmlFor="wallet"> Number of Tokens</label>
                          <input type="number" min="1" className="form-control" placeholder="Enter no of tokens" name="amount" value={this.state.amount || ''} onChange={this.toggleValidate} />
                          <div style={{ color: 'yellow' }}>{this.state.errors.amount}</div>
                        </div>
                        <div className="form-group">
                          <label htmlFor="wallet"> Ether wallet password </label>
                          <input type="password" className="form-control" placeholder="Ether wallet password" name="ethvalpwd" value={this.state.ethvalpwd || ''} onChange={this.toggleValidate} />
                          <div style={{ color: 'yellow' }}>{this.state.errors.ethvalpwd}</div>
                        </div>
                        <div className="form-group send-btn-bttm">
                          <button style={{ color: '#fff' }} type="button" className="btn btn-send" onClick={this.toggleTransToken} disabled={this.state.toAddress == '' || this.state.amount == '' || this.state.ethvalpwd == ''}>Send</button>
                        </div>
                      </form>
                    </div>
                  </div>
                </div>
              </div>
            </section>
          </section>
        </section>
        {/* <!--Burn Unsold Token--> */}
        {this.state.isBurn && <div className='loaderBg loaderimg'>
          <div className="burn-token-popup">
            <div className="burn-token-div">
              <h1>Burn Unsold Tokens</h1>
              <div className="burn-body-cont">
                <form action="" method="post">
                  <div className="form-group">
                    <label>No. of Tokens to be burnt:</label>
                    <input placeholder='No. of tokens to be burnt' type="number" min="1" name="amount2" value={this.state.amount2 || ''} onChange={this.toggleValidate} />
                    <div style={{ color: 'yellow' }}>{this.state.errors.amount2}</div>
                  </div>
                  <div className="form-group">
                    <label> Ether Wallet Password:</label>
                    <input placeholder='Ether Wallet Password' type="password" name="ethvalpwd2" value={this.state.ethvalpwd2 || ''} onChange={this.toggleValidate} />
                    <div style={{ color: 'yellow' }}>{this.state.errors.ethvalpwd2}</div>
                  </div>
                  <div className="form-group">
                    <button type="button" className="btn btn-cancel" onClick={() => this.setState({ isBurn: false })}>Cancel</button>
                    <button type="button" className="btn btn-confirm" onClick={this.toggleBurnToken} disabled={!this.state.amount2 || !this.state.ethvalpwd2}>Confirm</button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>}
        {this.state.icoTo && <div className='loaderBg loaderimg'>
          <div className="burn-token-popup">
            <div className="burn-token-div icoTokenDiv">
              <h1>ICO Tokens</h1>
              <div className="burn-body-cont table-responsive">
                {this.state.showingIcotokens.length > 0 ? (
                  <table className="table">
                    <thead>
                      <tr><th>Levels</th>
                        <th>Total ICO Tokens</th>
                        <th>Balance Tokens</th>
                        <th> Sold Tokens </th>
                      </tr>
                    </thead>
                    <tbody>
                      {
                        this.state.showingIcotokens.map((item, key) =>

                          <tr key={key}>
                            <td >{item.slabs}</td>
                            <td>{item.totalDistributionTokens}</td>
                            <td>{item.balanceCoins}</td>
                            <td>{item.soldTokens}</td>
                          </tr>
                        )
                      }
                    </tbody>
                  </table>) : (
                    <table>
                      <thead>No Details</thead>
                    </table>
                  )}
                <button type="button" className="btn btn-cancel" onClick={() => this.setState({ icoTo: false })}>Close</button>
              </div>
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
                        <input type="password" className="form-control" placeholder="Old Password" name="oldPassword" value={this.state.oldPassword || ''} onChange={this.handleChange} />
                        <div style={{ color: 'yellow' }}>{this.state.errors.oldPassword}</div>
                      </div>
                      <div className="form-group">
                        <input type="password" className="form-control" placeholder="New Password" name="password" value={this.state.password || ''} onChange={this.handleChange} />
                        <div style={{ color: 'yellow' }}>{this.state.errors.password}</div>
                      </div>
                      <div className="form-group">
                        <input type="password" className="form-control" placeholder="Confirm New password" name="confirmPassword" value={this.state.confirmPassword || ''} onChange={this.handleChange} />
                        <div style={{ color: 'yellow' }}>{this.state.errors.confirmPassword}</div>
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
        {this.state.isRefBouns && <div className="referral-bonus">
          <div className="referral-bonus-div">
            <h1>Referral Bonus</h1>
            <div className="burn-body-cont">
              <table className="table">
                <thead>
                  <tr>
                    <th>Levels Of Referral</th>
                    <th>Bonus</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>Level 1</td>
                    <td className="text-right">{this.state.refBouns.referralLevel1Tokens}</td>
                  </tr>
                  <tr>
                    <td>Level 2</td>
                    <td className="text-right">{this.state.refBouns.referralLevel2Tokens}</td>
                  </tr>
                  <tr>
                    <td>Level 3</td>
                    <td className="text-right">{this.state.refBouns.referralLevel3Tokens}</td>
                  </tr>
                  <tr>
                    <td>Level 4</td>
                    <td className="text-right">{this.state.refBouns.referralLevel4Tokens}</td>
                  </tr>
                  <tr>
                    <td className="texttotla">Total</td>
                    <td className="text-right">{this.state.refBouns.referralTokens}</td>
                  </tr>
                </tbody>
              </table>
              <button type="button" className="btnclose" onClick={() => { this.setState({ isRefBouns: false }) }}>Close</button>
            </div>
          </div>
        </div>}
      </div>
    )
  }
}
export default AdminDashboard;