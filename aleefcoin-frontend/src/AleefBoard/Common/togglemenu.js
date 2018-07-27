import React from 'react';

export default class ToggleMenu extends React.Component {
    constructor(props) {
        super(props);
    }
    togglemenu() {
        var element = document.getElementById("sidebar");
        var element1 = document.getElementById("main-content");

        if (element.classList) {
            element.classList.toggle("hide-left-bar");
            element1.classList.toggle("merge-left");
        }
    }
    render() {
        return (<button className="sidebar-toggle-box" onClick={() => {
            this.togglemenu();
        }}>
            <i className="fa fa-bars"></i>
        </button>)
    }
}
