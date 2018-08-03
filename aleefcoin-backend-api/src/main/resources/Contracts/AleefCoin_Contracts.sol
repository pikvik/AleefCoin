pragma solidity ^0.4.11;

interface ERC20 {
	function name() public  returns (string coinName);
	function symbol() public  returns (string decimals);
    	function decimals() public  returns (uint8 decimals);
    	function totalSupply() public  returns (uint256 totalSupply);
    	function transfer(address _to, uint256 _value) public returns (bool success);
   	function burn( uint256 _value) public returns (bool success);
    	function mint(address target, uint256 _value) public returns (bool success);
    	function transferFrom(address _from, address _to, uint256 _value) public returns (bool success); 
	function approve(address _spender, uint256 _value) public returns (bool success);
    	function allowance(address _owner, address _spender) public  returns (uint256 remaining);
}

contract AleefCoin is ERC20 {
	uint public  _totalSupply;
    	uint8 public  _decimals = 4;
    	string public  symbol;
    	string public  name;
    	uint256 public deleteToken;
    	uint256 public mintAmount;
    	uint256 public soldToken;
  	
	/* This creates an array with all balances */
    	mapping(address => mapping(address => uint256)) approved;
    	mapping (address => uint256) public balanceOf;

	/* Initializes contract with initial supply tokens to the creator of the contract */
    	function AleefCoin(string coinName,string coinSymbol,uint initialSupply) {         
      
        _totalSupply = initialSupply *10**uint256(_decimals);                        // Update total supply
        balanceOf[msg.sender] = _totalSupply; 
        name = coinName;                                   			     // Set the name for display purposes
        symbol =coinSymbol;                                                          // Set the symbol for display purposes
    	}
	
	function name() public  returns (string coinName){
 		return name;
	}

	function symbol() public  returns (string decimals){
		return symbol;
	}

    	function decimals() public  returns (uint8 decimals) {
        	return _decimals;
    	}

    	function totalSupply() public  returns (uint256 totalSupply) {
        	return _totalSupply;
    	}

	/* Send coins */
       function transfer(address _to, uint256 _value) public returns (bool success) {
        	require(balanceOf[msg.sender] >= _value && _value > 0);
            	if (_to == 0x0) revert();  
        	balanceOf[msg.sender] -= _value;
        	balanceOf[_to] += _value;
        	soldToken = soldToken + _value;
        	Transfer(msg.sender, _to, _value);
        	return true;
	}
    
    	function burn(uint256 _count) public returns (bool success) {
        	require(_count >0 );
        	if(deleteToken <0 )revert();					// Check if the sender has enough
        	balanceOf[msg.sender] -= _count;				// Subtract from the sender
        	Burn(msg.sender, _count);
        	deleteToken = deleteToken + _count;				// Updates totalSupply 
        	//burnToken = deleteToken;
		return true;
    	}
    
     	function mint(address target,uint256 _mintCount) public returns (bool success) {
     		require(_mintCount >0 );
         	if (target == 0x0) revert();  
         	balanceOf[msg.sender] += _mintCount;
       		mintAmount = mintAmount + _mintCount;
	    	return true;
    	}

	/* A contract attempts to get the coins */    
     	function transferFrom(address _from, address _to, uint256 _value) public returns (bool success) {
	  	if (_from == 0x0) revert();					// Prevent transfer to 0x0 address. 
	   	if (_to == 0x0) revert();  
        	require(approved[_from][msg.sender] >= _value && balanceOf[_from] >= _value && _value > 0);
        	balanceOf[_from] -= _value;					// Check if the sender has enough
        	balanceOf[_to] += _value;					// Check for overflows
        	approved[_from][msg.sender] -= _value;				// Check allowance
        	Transfer(_from, _to, _value);
        	return true;
    	}

	/* Allow another contract to spend some tokens in your behalf */
    	function approve(address _spender, uint256 _value) public returns (bool success) {
	 	if (_spender == 0x0) revert(); 
	  	require(_value >0 );
        	approved[msg.sender][_spender] = _value;
        	return true;
    	}

	function allowance(address _owner, address _spender) public  returns (uint256 remaining) {
   		if (_owner == 0x0) revert();
    		if (_spender == 0x0) revert(); 
        	return approved[_owner][_spender];
    	}

	/* This generates a public event on the blockchain that will notify clients */  
  	event Transfer(address indexed _from, address indexed _to, uint256 _value);

	/* This notifies clients about the amount burnt */
  	event Burn(address indexed from, uint256 value);
}