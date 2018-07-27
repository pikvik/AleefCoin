import React from 'react';
import { NavLink } from 'react-router-dom';

export default class Success extends React.Component {
    render() {
        return (
            <div>
                <section className="success">
                    <div className="successInner">
                        <img src="src/public/img/success.png" />
                        <h2>Success</h2>
                        <h5>Thanking you for register pre-ICO of aleefcoin.io</h5>
                        <h5>Our Team will update you periodically on offer and promotions</h5>
                        <p>Thank you</p>
                        <a>Info@aleefcoin.io</a>
                        <div className="SuccbtnBg"><NavLink to='/landpage' className="successBtn">Back To Home</NavLink></div>
                    </div>
                </section>
            </div>
        )
    }
}