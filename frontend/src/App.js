import React from "react";

export default function App() {
  return (
    <main style={{ fontFamily: "system-ui, sans-serif", padding: 24 }}>
      <h1>Frontend is live 🚀</h1>
      <p>Backend API base: {process.env.REACT_APP_API_URL || "(not set)"}</p>
    </main>
  );
}
