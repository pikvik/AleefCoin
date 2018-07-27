import React from 'react';

export default class Home extends React.Component {
    render() {
        return (
            <div>
                <header id="home" className="header">
                    <div className="container">
                        <div className="col-md-12">
                            <div className="col-md-6 col-sm-6 text-center">
                                <img src="src/public/img/bannerIcon1.png" className="bannerIcon" />
                            </div>
                            <div className="col-md-6 col-sm-6 text-left">
                                <h1 className="bannerHeadText upperCase">Come! Let's Prosper</h1>
                                <p className="bannerp">State of the art decentralized cryptocurrency built on an Ethereum platform to make minimum investments and maximum profits. </p>
                                <div className="aboutBannerDiv">
                                    <h2>Pre ICO Token sale</h2>
                                    <ul>
                                        <li className="rightBorder">
                                            <span>Started On</span>
                                            <span className="liSpan">5th July 2018</span>
                                        </li>
                                        <li>
                                            <span>Ends On</span>
                                            <span className="liSpan">18th August 2018</span>
                                        </li>
                                    </ul>
                                    <a href="https://portal.aleefcoin.io:8002/register" target="_blank"><button type="button" className="bannerBtn" data-toggle="modal">Register Now</button></a>
                                    <h3>Avail 50% free coins (Buy 2 Get 1 Free) on Pre-ICO Sale</h3>
                                    <p>(1 ALEEF Coin = USD $0.25)</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </header>
            </div>
        )
    }
}