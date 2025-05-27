import React, { useEffect, useRef } from 'react';

export default function RemoteVideo({ stream, name }) {
  const videoRef = useRef(null);

  useEffect(() => {
    if (videoRef.current && stream) {
      videoRef.current.srcObject = stream;
    }
  }, [stream]);

  return (
    <div className="video-box">
      <video ref={videoRef} autoPlay playsInline className="video" />
      <div className="name-tag">{name}</div>
    </div>
  );
}
