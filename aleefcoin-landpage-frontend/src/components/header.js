import React from 'react';
import { ScaleLoader } from 'react-spinners';
import Notifications, { notify } from 'react-notify-toast';
import { NavLink } from 'react-router-dom';

export default class Header extends React.Component {
	render() {
		return (
			<div>
				<nav className="navbar navbar-fixed-top" id="mainNav">
					<div className="container">
						<div className="navbar-header">
							<Notifications />
							{this.props.loading && <div className='loaderBg'>
								<div className='loaderimg'>
									<ScaleLoader
										size={180}
										color={'#fff'}
										loading={this.props.loading}
									/>
								</div>
							</div>}
							<button type="button" className="navbar-toggle js-scroll-trigger" data-toggle="collapse" data-target="#myNavbar">
								<span className="icon-bar"></span>
								<span className="icon-bar"></span>
								<span className="icon-bar"></span>
							</button>
							<a className="navbar-brand" href="#"><img src="src/public/img/logo.png" /></a>
						</div>
						<div className="collapse navbar-collapse" id="myNavbar">
							<ul className="nav navbar-nav navbar-right">
								<li><NavLink to='/landpage#home' className="js-scroll-trigger">Home</NavLink></li>
								<li><NavLink to='/landpage' className="js-scroll-trigger">About Us </NavLink></li>
								<li><NavLink to='/landpage#ico' className="js-scroll-trigger">ICO/Referrals </NavLink></li>
								<li><NavLink to='/landpage' className="js-scroll-trigger">White Paper</NavLink></li>
								<li><NavLink to='/landpage' className="js-scroll-trigger">Road Map</NavLink></li>
								<li><NavLink to='/landpage' className="js-scroll-trigger">Our Team</NavLink></li>
								<li><NavLink to='/landpage' className="js-scroll-trigger">Features</NavLink></li>
								<li><NavLink to='/landpage' className="js-scroll-trigger">Wallet</NavLink></li>
								<li><a href="#contact" className="js-scroll-trigger">Contact Us</a></li>
							</ul>
						</div>
					</div>
				</nav>
			</div>
		)
	}
}