import React from 'react';
import { NavLink } from 'react-router-dom';
import queryString from 'query-string';
import Notifications, { notify } from 'react-notify-toast';
import { API_BASE_URL } from '../Common/apiUrl';
import axios from 'axios';

export default class TransactionSuccess extends React.Component {
    constructor(props) {
        super(props);
        const parsed = queryString.parse(props.location.search);
        this.state = {
            transferId: parsed.id,
            res: ''
        }
    }
    componentWillMount() {
        let payload = {
            transferId: this.state.transferId,
        }
        const approveUrl = API_BASE_URL + "token/transfer/approval";
        axios.post(approveUrl, payload)
            .then(response => {
                if (response.status == 200) {
                    this.setState({ res: response.data.message })
                    notify.show(response.data.message, 'success');
                }
                if (response.status == 206) {
                    this.setState({ res: response.data.message })
                    notify.show(response.data.message, 'error');
                }
            })
            .catch(function (error) {
                console.log(error);
            });
    }

    render() {
        return (
            <div>
                <Notifications />
                <NavLink to={'/login'}> Click to login </NavLink>
                <div className="success-message hide">
                    <div className="success-div">
                        <svg id="successAnimation" className="animated" width="110" height="110" viewBox="0 0 70 70">
                            <path id="successAnimationResult" fill="#D8D8D8" d="M35,60 C21.1928813,60 10,48.8071187 10,35 C10,21.1928813 21.1928813,10 35,10 C48.8071187,10 60,21.1928813 60,35 C60,48.8071187 48.8071187,60 35,60 Z M23.6332378,33.2260427 L22.3667622,34.7739573 L34.1433655,44.40936 L47.776114,27.6305926 L46.223886,26.3694074 L33.8566345,41.59064 L23.6332378,33.2260427 Z"
                            />
                            <circle id="successAnimationCircle" cx="35" cy="35" r="24" stroke="#979797" strokeWidth="2" strokeLinecap="round" fill="transparent"
                            />
                            <polyline id="successAnimationCheck" stroke="#979797" strokeWidth="2" points="23 34 34 43 47 27" fill="transparent" />
                        </svg>
                        <div className="success-content">
                            <p>{this.state.res}</p>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}