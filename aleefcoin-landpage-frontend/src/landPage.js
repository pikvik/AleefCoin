import React from 'react';
import $ from 'jquery';
import validator from 'validator';
import axios from 'axios';
import Notifications, { notify } from 'react-notify-toast';
import IntlTelInput from 'react-intl-tel-input';
import 'react-intl-tel-input/dist/libphonenumber.js';
import 'react-intl-tel-input/dist/main.css';
import { ScaleLoader } from 'react-spinners';
import { NavLink } from 'react-router-dom';
import Contactus from './components/contactus';
import { API_BASE_URL } from '../src/public/constants/ApiUrl';
import Header from './components/header';
import Footer from './components/footer';
import Home from './components/home';
import AboutUs from './components/aboutus';
import IcoReferals from './components/icoReferals';
import WhitePaper from './components/whitePaper';
import RoadMap from './components/roadMap';
import Team from './components/team';
import Feature from './components/feature';
import Wallet from './components/wallet';
import SocialShare from './components/socialShare';

export default class LandPage extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			no_days: "",
			no_hours: '',
			no_minutes: '',
			no_seconds: '',
			mobileNo: '',
			emailId: '',
			userName: '',
			errors: { userName: '', emailId: '', mobileNo: '' },
			userNameValid: false,
			emailIdValid: false,
			mobileNoValid: false,
			formValid: false,
			loading: false,
			isChecked: false,
			Checked: ''
		}
		this.getCountdown1 = this.getCountdown1.bind(this);
		this.getCountdown = this.getCountdown.bind(this);
		this.pad = this.pad.bind(this);
		this.handleChange = this.handleChange.bind(this);
		this.registerPreSale = this.registerPreSale.bind(this);
		this.checkChange = this.checkChange.bind(this);
		this.mobileNoHandler = this.mobileNoHandler.bind(this);
	}

	componentDidMount() {
		$(document).ready(function () {
			$("#myBtn3").click(function () {
				$("#myModal3").modal({ backdrop: "static" });
			});
		});
		// When the user scrolls down 20px from the top of the document, show the button
		$(window).scroll(function () {
			var height = $(window).scrollTop();

			if (height > 800) {
				$('#myBtn').fadeIn();
			} else {
				$('#myBtn').fadeOut();
			}
		});
		$(document).ready(function () {
			$("#myBtn").click(function (event) {
				event.preventDefault();
				$("html, body").animate({ scrollTop: 0 }, "slow");
				return false;
			});

		});
		this.getCountdown1();
	}

	// Aleef coin pre-ICO countdown timer

	getCountdown1() {
		var target_date = new Date("19 August, 2018");
		this.getCountdown(target_date);
		setInterval(() => {
			var target_date = new Date("19 August, 2018");
			this.getCountdown(target_date);
		}, 1000);
	}

	getCountdown(target_date) {
		var current_date = new Date().getTime();
		var seconds_left = (target_date - current_date) / 1000;

		this.setState({ no_days: this.pad(parseInt(seconds_left / 86400)) });
		seconds_left = seconds_left % 86400;

		this.setState({ no_hours: this.pad(parseInt(seconds_left / 3600)) });
		seconds_left = seconds_left % 3600;

		this.setState({ no_minutes: this.pad(parseInt(seconds_left / 60)) });
		this.setState({ no_seconds: this.pad(parseInt(seconds_left % 60)) });
	}

	pad(n) {
		return (n < 10 ? '0' : '') + n;
	}

	handleChange(e) {
		const value = e.target.value;
		const name = e.target.name;
		this.setState({ [name]: value },
			() => { this.validateField(name, value) });
	}

	// validation 

	validateField(fieldName, value) {
		let fieldValidationErrors = this.state.errors;
		let userNameValid = this.state.userNameValid;
		let emailIdValid = this.state.emailIdValid;
		switch (fieldName) {
			case 'userName':
				userNameValid = !(value.length < 6 || value.length > 20);
				fieldValidationErrors.userName = userNameValid ? '' : 'Username should be 6 to 20 characters';
				break;
			case 'emailId':
				emailIdValid = value.match(/^([\w.%+-]+)@([\w-]+\.)+([\w]{2,})$/i);
				fieldValidationErrors.emailId = emailIdValid ? '' : ' Please enter valid email id';
				break;
			default:
				break;
		}

		this.setState({
			errors: fieldValidationErrors,
			userNameValid: userNameValid,
			emailIdValid: emailIdValid,
		}, this.validateForm);
	}

	validateForm() {
		this.setState({ formValid: this.state.userNameValid && this.state.emailIdValid })
	}

	registerPreSale() {

		let payload = {
			'userName': this.state.userName,
			'emailId': this.state.emailId,
			'mobileNo': this.state.mobileNo
		}
		let register = API_BASE_URL + 'pre/register/ico';
		this.setState({ loading: true })
		axios.post(register, payload)
			.then(res => {
				this.setState({ loading: false })
				this.setState({ formValid: false })
				this.setState({ userName: '', emailId: '', mobileNo: '', isChecked: false })
				if (res.data.status == 'Success') {
					this.props.history.push('/sucess');
				}
				else if (res.data.status == 'Failure') {
					notify.show(res.data.message, 'error')
				}
			})
			.catch(function (error) {
				// console.log(error)
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
				<nav className="navbar navbar-fixed-top" id="mainNav">
					<div className="container">
						<div className="navbar-header">
							<Notifications />
							<button type="button" className="navbar-toggle js-scroll-trigger" data-toggle="collapse" data-target="#myNavbar">
								<span className="icon-bar"></span>
								<span className="icon-bar"></span>
								<span className="icon-bar"></span>
							</button>
							<a className="navbar-brand" href="#"><img src="src/public/img/logo.png" /></a>
						</div>
						<div className="collapse navbar-collapse" id="myNavbar">
							<ul className="nav navbar-nav navbar-right">
								<li><a href="#home" className="js-scroll-trigger">Home</a></li>
								<li><a href="#aboutUs" className="js-scroll-trigger">About Us </a></li>
								<li><a href="#ico" className="js-scroll-trigger">ICO/Referral </a></li>
								<li><a href="#white" className="js-scroll-trigger">White Paper</a></li>
								<li><a href="#roadmap" className="js-scroll-trigger">Road Map</a></li>
								<li><a href="#team" className="js-scroll-trigger">Team</a></li>
								<li><a href="#feature" className="js-scroll-trigger">Features</a></li>
								<li><a href="#wallet" className="js-scroll-trigger">Wallet</a></li>
								<li><a href="#contact" className="js-scroll-trigger">Contact Us</a></li>
								<li><a href="https://portal.aleefcoin.io:8002/login" target="_blank" className="js-scroll-trigger">Login</a></li>
							</ul>
						</div>
					</div>
				</nav>
				<Home />
				<AboutUs />
				<section id="">
					<div className="ico-hasstart-wrapper">
						<div className="container">
							<div className="row">
								<div className="col-lg-12 col-xs-12">
									<div className="ico_start">
										<div className="col-lg-12 text-center">
											<h1 className="page-title ico-title 	animated zoomIn visible" data-animation="zoomIn"> ICO SALE STARTS ON </h1>
										</div>
									</div>
									<div className="ico-hasst">
										<div className="row">
											<div className="col-lg-12">
												<div className="ico-list">
													<ul>
														<li className="clr1">
															<span id="days">{this.state.no_days}</span>
															<span className="rdc">DAYS</span>
														</li>
														<li className="clr2">
															<span id="hours">{this.state.no_hours}</span>
															<span className="rdc">HOURS</span>
														</li>
														<li className="clr3">
															<span id="minutes">{this.state.no_minutes}</span>
															<span className="rdc">MINUTES</span>
														</li>
														<li className="clr4">
															<span id="seconds">{this.state.no_seconds}</span>
															<span className="rdc">SECONDS</span>
														</li>
													</ul>
												</div>
											</div>
											<div className="col-lg-12">
												<div className="ico-sedate">
													<p>Starts on 19th August 2018</p>
													<p>Ends on 22nd October 2018</p>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</section>
				<button id="myBtn" title="Go to top"><img src="src/public/img/up-arrow.png" width="15px" /></button>
				<IcoReferals />
				<WhitePaper />
				<RoadMap />
				<Team />
				<Feature />
				<Wallet />
				<section>
					<div className="our_pricing content_txt" id="">
						<div className="container">
							<div className="row">
								<div className="our_price_tlt">
									Aleef Coin Listings
       								</div>
							</div>
						</div>
					</div>
					<div className="container">
						<ul id="flexiselDemo2">
							<li><a href="https://allcryptotalk.com/index.php?/topic/10288-ann-ico-aleef-coina-decentralised-blockchain-solution-for-promising-return/&tab=comments#comment-44658" target="_blank"><img src="src/public/img/affilates/1.png" /></a></li>
							<li><a href="https://cryptocurrencytalk.com/topic/107054-ann-ico-aleef-coina-decentralised-blockchain-solution-for-promising-return/" target="_blank"><img src="src/public/img/affilates/2.png" /></a></li>
							<li><a href="https://coincodex.com/crypto/aleef-coin/" target="_blank"><img src="src/public/img/affilates/3.png" /></a></li>
							<li><a href="https://icobuffer.com/projects/aleef-coin-ico" target="_blank"><img src="src/public/img/affilates/4.png" /></a></li>
							<li><a href="https://foundico.com/ico/aleef-coin.html" target="_blank"><img src="src/public/img/affilates/5.png" /></a></li>
							<li><a href="https://icolink.com/real-estate-ico-list/ad/aleef-coin-ico,1266.html" target="_blank"><img src="src/public/img/affilates/6.png" /></a></li>
							<li><a href="https://icoholder.com/en/aleef-coin-23080" target="_blank"><img src="src/public/img/affilates/7.svg" /></a></li>

						</ul>
					</div>
				</section>

				<div className="our_pricing content_txt" id="contact">
					<div className="container">
						<div className="row">
							<div className="our_price_tlt">
								CONTACT<span> US</span>
							</div>

						</div>
					</div>
				</div>
				<div className="contact-form">
					<div className="container">
						<div className="row">
							<Contactus />
							<div className="col-md-3 ">
								<ul className="add_location">
									<li><h4>Our Location</h4>
										<p> # 109, AL TAYERA BULDING, BUR DUBAI, DUBAI. UAE.</p>
									</li>

									<li><h4>Connect Online</h4>
										<p>Email : info@aleefcoin.io <br /> Website : www.aleefcoin.io</p>
									</li>
								</ul>
								<div className="socialDiv">
									<h4>Stay Connected</h4>
									<ul>
										<li>
											<a href="https://www.facebook.com/aleefcoinofficial/" target='_blank'>
												<img src="src/public/img/facebook.png" />
											</a>
										</li>
										<li>
											<a href="https://twitter.com/aleef_coin" target='_blank'>
												<img src="src/public/img/twitter.png" />
											</a>
										</li>

										<li>
											<a href="https://www.linkedin.com/company/aleef-coin/" target='_blank'>
												<img src="src/public/img/linkedin.png" />
											</a>
										</li>
										<li>
											<a href="https://plus.google.com/u/0/100441992264634701075?iso=false" target='_blank'>
												<img src="src/public/img/google-plus.png" />
											</a>
										</li>
										<li>
											<a href="https://t.me/aleefcoinofficial" target='_blank'>
												<img src="src/public/img/telegram.png" />
											</a>
										</li>
										<li>
											<a href="https://www.instagram.com/Aleef_Coin_Official/" target='_blank'>
												<img src="src/public/img/instagram-logo.png" />
											</a>
										</li>
										<li>
											<a href="https://www.youtube.com/channel/UC5Xk3sBYzivpqY55ZzQ2bAQ" target='_blank'>
												<img src="src/public/img/youtube.png" />
											</a>
										</li>
										<li>
											<a href="https://medium.com/@aleefcoinofficial" target='_blank'>
												<img src="src/public/img/medium-size.png" />
											</a>
										</li>
										<li>
											<a href="https://www.quora.com/profile/Aleef-Coin-Official" target='_blank'>
												<img src="src/public/img/quora.png" />
											</a>
										</li>
										<li>
											<a href=" https://www.reddit.com/user/aleefcoinofficial" target='_blank'>
												<img src="src/public/img/reddit.png" />
											</a>
										</li>
										<li>
											<a href="https://bitcointalk.org/index.php?action=profile;u=2171634" target='_blank'>
												<img src="src/public/img/bitcoin-logo.png" />
											</a>
										</li>
									</ul>
								</div>
							</div>
						</div>
					</div>
				</div>
				<SocialShare />
				<Footer />
				<div id="preRegister" className="modal fade" role="dialog">
					<div className="modal-dialog">
						{/* <!-- Modal content--> */}
						<div className="modal-content">
							<div className="modal-header modelHead">
								<button type="button" className="close" data-dismiss="modal">&times;</button>
								<h4 className="modal-title">PRESALE FORM</h4>
							</div>
							<div className="modal-body">
								<form>
									<div className="col-md-12 form-group">
										<label>Name</label>
										<input type="text" name='userName' value={this.state.userName} onChange={this.handleChange} className="form-control" />
										<div style={{ color: "red" }}>{this.state.errors.userName}</div>
									</div>
									<div className="col-md-12 form-group">
										<label>Email Id</label>
										<input type="text" name='emailId' value={this.state.emailId} onChange={this.handleChange} className="form-control" />
										<div style={{ color: "red" }}>{this.state.errors.emailId}</div>
									</div>
									<div className="col-md-12 form-group">
										<label>Mobile</label>
										<IntlTelInput
											name='mobileNo'
											value={this.state.mobileNo}
											onChange={this.handleChange}
											onPhoneNumberChange={this.mobileNoHandler}
											onPhoneNumberBlur={this.mobileNoHandler}
											css={['intl-tel-input', 'form-control']}
											utilsScript={'libphonenumber.js'}
										/>
										<div style={{ color: "red" }}>{this.state.errors.mobileNo}</div>
									</div>
									<div className="col-md-12  form-group">
										<div className="cntr">
											<input className="hidden-xs-up" id="cbx" type="checkbox" checked={this.state.isChecked} onChange={this.checkChange} required />
											<label className="cbx" htmlFor="cbx"></label>
											<label className="lbl" htmlFor="cbx">I agree the
											<NavLink to='/termsofservice' target='_blank' > Terms Of Service</NavLink> and
											<NavLink to='/privacypolicy' target='_blank'> Privacy Policy</NavLink></label>
											{!this.state.isChecked && <div style={{ color: "red" }}>{this.state.Checked}</div>}
										</div>
									</div>
									{!this.state.formValid || !this.state.isChecked || !this.state.mobileNoValid ?
										<button type="button" className="submitBtn" style={{ cursor: 'no-drop' }} disabled={!this.state.formValid || !this.state.isChecked || !this.state.mobileNoValid} onClick={this.registerPreSale} data-dismiss="modal">Submit</button> :
										<button type="button" className="submitBtn" disabled={!this.state.formValid || !this.state.isChecked || !this.state.mobileNoValid} onClick={this.registerPreSale} data-dismiss="modal">Submit</button>
									}
								</form>
							</div>
						</div>
					</div>
				</div>
			</div >
		)
	}
}
