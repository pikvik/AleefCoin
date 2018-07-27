import React from 'react';

export default class Feature extends React.Component {
    render() {
        return (
            <div>
                <section id="feature">
                    <div className="container">
                        <h2 className="upperCase abouth2">Features</h2>
                        <h5 className="abouth2 lineHeight">ALEEF coin provides you the best cryptocurrency with advanced technology with many features</h5>
                        <div className="col-md-12">
                            <div className="col-md-4">
                                <div className="col-md-8 col-sm-8 col-xs-12 text-right">
                                    <h5 className="upperCase fontWeight hhh">CONVENIENCE</h5>
                                    <p>Tokens are integrated into tokenized wallets, will be utilized on stock markets, and will eventually be able to be integrated with existing stores.</p>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-12">
                                    <img src="src/public/img/feature1.png" className="featureImg" />
                                </div>
                                <div className="col-md-8 col-sm-8 col-xs-12 text-right">
                                    <h5 className="upperCase fontWeight hhh">LOWER FEES </h5>
                                    <p>Bank transactions and Credit card processing companies charge hefty fees. But it is not the case with ALEEF Coin as the costs are nil or negligible</p>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-12">
                                    <img src="src/public/img/feature2.png" className="featureImg" />
                                </div>
                                <div className="col-md-8 col-sm-8 col-xs-12 text-right">
                                    <h5 className="upperCase fontWeight hhh">USE OF BLOCKCHAIN ARCHITECTURE</h5>
                                    <p>Emission of ALEEF coin tokens is done on the Ethereum blockchain, which is among the most stable on the market today. A safe and secure algorithm built by Ethereum enhance a decentralized platform for all transactions</p>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-12">
                                    <img src="src/public/img/feature3.png" className="featureImg" />
                                </div>
                            </div>
                            <div className="col-md-4">
                                <img src="src/public/img/feature.png" className="featureImg" />
                            </div>
                            <div className="col-md-4">
                                <div className="col-md-4 col-sm-4 col-xs-12">
                                    <img src="src/public/img/feature1.png" className="featureImg" />
                                </div>
                                <div className="col-md-8 col-sm-8 col-xs-12 text-left">
                                    <h5 className="upperCase fontWeight hhh">EASE OF USE</h5>
                                    <p>ALEEF coin is being developed as an easy to use token with an understandable Ether equivalent. It does not require specialized knowledge in cryptography or blockchain technology.</p>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-12">
                                    <img src="src/public/img/feature2.png" className="featureImg" />
                                </div>
                                <div className="col-md-8 col-sm-8 col-xs-12 text-left">
                                    <h5 className="upperCase fontWeight hhh">QUICK PAYMENTS </h5>
                                    <p>Making payments using ALEEF Coin is very easy.  You can do it in just a matter of a few seconds. It is very fast because you don’t require to feed many details</p>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-12">
                                    <img src="src/public/img/feature3.png" className="featureImg" />
                                </div>
                                <div className="col-md-8 col-sm-8 col-xs-12 text-left">
                                    <h5 className="upperCase fontWeight hhh">IDENTITY THEFT </h5>
                                    <p>Nobody can steal your personal information from merchants, which ensures the privacy of your sensitive data by creating a proxy ID.</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        )
    }
}