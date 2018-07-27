import React from 'react';

export default class Wallet extends React.Component {
    render() {
        return (
            <div>
                <section id="wallet">
                    <div className="container">
                        <h2 className="abouth2 upperCase white">Wallet</h2>
                        <div className="col-md-12">
                            <div className="col-md-6 col-sm-6">
                                <img src="src/public/img/holding-phone.png" className="walletImg" />
                            </div>
                            <div className="col-md-6 col-sm-6 text-left">
                                <h3 className="upperCase white fontWeight marginTop">Will be available on google play and app store Shortly</h3>
                                <p className="walletp">With just few finger tips, you can now manage your investments using ALEEF Coin Mobile Wallet</p>
                                <div className="andriod"><img src="src/public/img/googlePlay.png" className="googlePlay" />
                                    <img src="src/public/img/apple.png" className="apple" /></div>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        )
    }
}