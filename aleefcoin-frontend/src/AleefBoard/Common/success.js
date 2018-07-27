import React from 'react';
import { NavLink } from 'react-router-dom';

export default class Success extends React.Component {
    render() {
        return (
            <div>
                <section className="success">
                    <div className="successInner">
                        <img src="src/public/image/success.png" />
                        <h2>Success</h2>
                        <h5>Thanking you for registering with aleefcoin.io</h5>
                        <h6>Your account is activated now, Please click below button to Login and purchase Aleef Coin</h6>
                        <p>Thank you</p>
                        <a>Info@aleefcoin.io</a>
                        <div className="SuccbtnBg"><NavLink to='/login' className="successBtn">Click To Log in</NavLink></div>
                    </div>
                </section>
            </div>
        )
    }
}