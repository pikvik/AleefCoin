import React from 'react';
import Header from './header';
import Footer from './footer';
import { NavLink } from 'react-router-dom';
import SocialShare from './socialShare';

export default class Faq extends React.Component {
	render() {
		return (
			<div>
				<section className="contentSection">
					<div className="banner">
						<div className="container">
							<h2>FAQ</h2>
							<span className="breadcrumbs"><NavLink to='/landpage'>Home</NavLink>&nbsp; /  &nbsp; FAQ</span>
						</div>
					</div>
					<div className="contentDetailsBg">
						<div className="container">
							<div className="bs-example">
								<div className="panel-group" id="accordion">
									<div className="panel panel-default">
										<div className="panel-heading">
											<h4 className="panel-title">
												<a data-toggle="collapse" data-parent="#accordion" href="#collapse1">Who is Aleef? <span className="glyphicon glyphicon-plus"></span></a>
											</h4>
										</div>
										<div id="collapse1" className="panel-collapse collapse">
											<div className="panel-body">
												<p>Aleef Group of companies is a 2 decades old group of companies the group has footprints in Dubai & India into several lucrative businesses with a vision of growth of society and catering to basic needs at an affordable price. The group is presently in multiple businesses mainly import & export, Healthcare, Real estate, organic foods, and education. The group believes in where Honesty is the only policy.</p>
											</div>
										</div>
									</div>
									<div className="panel panel-default">
										<div className="panel-heading">
											<h4 className="panel-title">
												<a data-toggle="collapse" data-parent="#accordion" href="#collapse2"> What is Cryptocurrency? <span className="glyphicon glyphicon-plus"></span></a>
											</h4>
										</div>
										<div id="collapse2" className="panel-collapse collapse">
											<div className="panel-body">
												<p>A cryptocurrency (or crypto currency) is a digital asset designed to work as a medium of exchange that uses cryptography to secure its transactions, to control the creation of additional units, and to verify the transfer of assets.</p>
											</div>
										</div>
									</div>
									<div className="panel panel-default">
										<div className="panel-heading">
											<h4 className="panel-title">
												<a data-toggle="collapse" data-parent="#accordion" href="#collapse3">What is Aleef Coin?<span className="glyphicon glyphicon-plus"></span></a>
											</h4>
										</div>
										<div id="collapse3" className="panel-collapse collapse">
											<div className="panel-body">
												<p>ALEEF Coin is a new cryptocurrency based on the Ethereum Blockchain that will be distributed through our channel partners and made available to everyone, anytime, anywhere. This is the core of our business, and market development will be at the center of our strategy. </p>
												<p>ALEEF Coin is the First cryptocurrency to revolutionize the payments and investments industry, synergies between the reach of investors and the fast, borderless nature of Blockchain technology.</p>
											</div>
										</div>
									</div>
									<div className="panel panel-default">
										<div className="panel-heading">
											<h4 className="panel-title">
												<a data-toggle="collapse" data-parent="#accordion" href="#collapse4">What is an ICO?<span className="glyphicon glyphicon-plus"></span></a>
											</h4>
										</div>
										<div id="collapse4" className="panel-collapse collapse">
											<div className="panel-body">
												<p>Like an initial public offering (IPO) of stock, from which its name is derived, an ICO is a way that cryptocurrency startups -- and even established companies -- can raise Capital..  </p>
											</div>
										</div>
									</div>
									<div className="panel panel-default">
										<div className="panel-heading">
											<h4 className="panel-title">
												<a data-toggle="collapse" data-parent="#accordion" href="#collapse5">May I know the ICO parameters of Aleef Coin?<span className="glyphicon glyphicon-plus"></span></a>
											</h4>
										</div>
										<div id="collapse5" className="panel-collapse collapse">
											<div className="panel-body">
												<ul>
													<li>Coin Name: ALEEF Coin</li>
													<li>Coin Symbol: ALEEF</li>
													<li>Total number of mined coins for distribution: 300 Million</li>
													<li>Total number of mined coins available during Pre-ICO and ICO launch: 247 Million</li>
													<li>PRE ICO @  0.25$ with 50% free coins (38 million +15.4 million free coins total coins 53.4 million)</li>
													<li>PRE ICO : From 5th July 2018 to 18th August 2018</li>
													<li>ICO : From 19th August 2018 to 22nd October 2018</li>
													<li>Coin value starts at 1 ALEEF</li>
													<li>No minimum cap per day for user purchase</li>
												</ul>
											</div>
										</div>
									</div>
									<div className="panel panel-default">
										<div className="panel-heading">
											<h4 className="panel-title">
												<a data-toggle="collapse" data-parent="#accordion" href="#collapse6">What is the token distribution campaign structure and free coins percentage?<span className="glyphicon glyphicon-plus"></span></a>
											</h4>
										</div>
										<div id="collapse6" className="panel-collapse collapse">
											<div className="panel-body">
												<p>We have the following campaign structure: </p>
												<p>TBD as per portal </p>
											</div>
										</div>
									</div>
									<div className="panel panel-default">
										<div className="panel-heading">
											<h4 className="panel-title">
												<a data-toggle="collapse" data-parent="#accordion" href="#collapse7">Is there a hard cap for the campaign?<span className="glyphicon glyphicon-plus"></span></a>
											</h4>
										</div>
										<div id="collapse7" className="panel-collapse collapse">
											<div className="panel-body">
												<p>The campaign hard cap is 300,000,000. Once this goal has been achieved the token distribution campaign will be stopped. </p>
											</div>
										</div>
									</div>
									<div className="panel panel-default">
										<div className="panel-heading">
											<h4 className="panel-title">
												<a data-toggle="collapse" data-parent="#accordion" href="#collapse8">What is the soft cap for your project?<span className="glyphicon glyphicon-plus"></span></a>
											</h4>
										</div>
										<div id="collapse8" className="panel-collapse collapse">
											<div className="panel-body">
												<p>The soft cap is 5,000,000.</p>
											</div>
										</div>
									</div>
									<div className="panel panel-default">
										<div className="panel-heading">
											<h4 className="panel-title">
												<a data-toggle="collapse" data-parent="#accordion" href="#collapse9">When will your tokens be available on exchange platforms?<span className="glyphicon glyphicon-plus"></span></a>
											</h4>
										</div>
										<div id="collapse9" className="panel-collapse collapse">
											<div className="panel-body">
												<p>As soon as the ICO is completed,. <strong>Expected exchange listing is in November 2018</strong>.</p>
											</div>
										</div>
									</div>
									<div className="panel panel-default">
										<div className="panel-heading">
											<h4 className="panel-title">
												<a data-toggle="collapse" data-parent="#accordion" href="#collapse10">Are your tokens compliant with ERC20? <span className="glyphicon glyphicon-plus"></span></a>
											</h4>
										</div>
										<div id="collapse10" className="panel-collapse collapse">
											<div className="panel-body">
												<p>Yes, our tokens are compliant with ERC20. </p>
											</div>
										</div>
									</div>
									<div className="panel panel-default">
										<div className="panel-heading">
											<h4 className="panel-title">
												<a data-toggle="collapse" data-parent="#accordion" href="#collapse11">Do I get any benefit if I refer Aleef coin to anyone?<span className="glyphicon glyphicon-plus"></span></a>
											</h4>
										</div>
										<div id="collapse11" className="panel-collapse collapse">
											<div className="panel-body">
												<p>Yes, Aleef coin offers 4 Level of Referral bonus program like 10%, 5%, 3%, 2% for Level 1, Level 2, Level 3 and Level 4 respectively. Please check the whitepaper for more details.</p>
											</div>
										</div>
									</div>
									<div className="panel panel-default">
										<div className="panel-heading">
											<h4 className="panel-title">
												<a data-toggle="collapse" data-parent="#accordion" href="#collapse12">What could be Return-on-investment (RoI) or any profit sharing?<span className="glyphicon glyphicon-plus"></span></a>
											</h4>
										</div>
										<div id="collapse12" className="panel-collapse collapse">
											<div className="panel-body">
												<p>Yes, One of the major business of Aleef group is real estate.  Real estate is generally a great investment option. It can generate an ongoing <a href="">passive income</a>, and it can prove to be a good long-term investment if its value increases exponentially over time. By keeping in mind, Aleef invest on real estate and the profit will be shared to all the investors. The value of profit will be shared as Aleef coins to its investors.</p>
												<p>Those who invest in pre ICO itself can expect min 3X to 4X return on investments as once it gets listed the value will be above $ 1 .00 . There are several utilities planned for those buying Aleef coin as apart from trading income people globally will be using the coins to buy products & services. These utilities solve specific pains in the society or improve the functionality or for leisure from young to old generation. Once utilities are on the demand for Aleef coins it  will automatically increase the coin rate so even long term investors can get benefit . Aleef Group is a very flexible group and understands the needs of people and would genuinely wants to contribute to the benefits of people globally.</p>
												<p>Moreover those good in network marketing or direct marketing can also benefit by joining the referral program and earn handsomely.</p>
											</div>
										</div>
									</div>
									<div className="panel panel-default">
										<div className="panel-heading">
											<h4 className="panel-title">
												<a data-toggle="collapse" data-parent="#accordion" href="#collapse13">What is the marketing plan for Aleef coins?<span className="glyphicon glyphicon-plus"></span></a>
											</h4>
										</div>
										<div id="collapse13" className="panel-collapse collapse">
											<div className="panel-body">
												<p>Aleef has made an extensive marketing plan for its coins by using latest trends in social media marketing, data sense/ adwords , email marketing to large investors, holding one to one meetings with investors community, ERC platform investors , holding public seminars in major metros , crypto based news like Bitcoin news , etc ,App based marketing like push notifications, scrolls on digital exchanges to woo investors  and also print media .  Aleef is not leaving any iota of marketing its brand. </p>
											</div>
										</div>
									</div>
									<div className="panel panel-default">
										<div className="panel-heading">
											<h4 className="panel-title">
												<a data-toggle="collapse" data-parent="#accordion" href="#collapse14">How Aleef is different from other ICO’s?<span className="glyphicon glyphicon-plus"></span></a>
											</h4>
										</div>
										<div id="collapse14" className="panel-collapse collapse">
											<div className="panel-body">
												<p>Aleef is different cause it one of the first property based ICO making it 100 % secure for investors, guaranteed growth coupled with 2 decades of integrity and ethics . Aleef has the people behind the show names and their linked in accounts which can be checked and physical office address declared on website making it 100 % transparent and easily verifiable. Moreover the company is using the best time proven technology with certifications and testing procedures required by digital exchanges. Aleef is also committed to Anti Money laundering act and will be keeping KYC of investors as per recent amendments in crypto currency rules globally.</p>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</section>
				<SocialShare />
				<Footer />
			</div>
		)
	}
}