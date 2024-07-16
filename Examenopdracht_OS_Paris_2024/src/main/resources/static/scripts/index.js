  function toggleTableVisibility() {
        var table = document.getElementById("ticketTable");
        table.style.display = table.style.display === "none" ? "table" : "none"; 
        table.scrollIntoView({ behavior: 'smooth' });
        
        var dropDown = document.getElementById("dropDown");
        dropDown.style.transform= table.style.display==="table"?"rotate(180deg)":"rotate(0deg)";
  }
 