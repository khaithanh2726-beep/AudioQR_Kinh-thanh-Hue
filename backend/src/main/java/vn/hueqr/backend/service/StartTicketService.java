package vn.hueqr.backend.service;

import java.security.MessageDigest;
import java.time.Clock;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import vn.hueqr.backend.dto.StartTicketResponse;
import vn.hueqr.backend.service.StartTicketCodec.Payload;

@Service
public class StartTicketService {
	private final StartTicketCodec codec;
	private final StartTicketTimePolicy time;
	private final GuestInputPolicy input;
	private final UUID epoch;
	private final Clock clock;

	public StartTicketService(StartTicketCodec codec, StartTicketTimePolicy time, GuestInputPolicy input,
			@Qualifier("guestServiceEpoch") UUID epoch, Clock clock) {
		this.codec = codec;
		this.time = time;
		this.input = input;
		this.epoch = epoch;
		this.clock = clock;
	}

	public StartTicketResponse issue(String credential) {
		byte[] hash = input.credentialHash(credential);
		Payload payload = new Payload(UUID.randomUUID(), hash, epoch, time.issueTimes());
		return new StartTicketResponse(codec.encode(payload), payload.requestId(), payload.times().issuedAt(),
				payload.times().ticketExpiresAt(), epoch, clock.instant());
	}

	public Payload verify(String ticket, UUID key, byte[] credentialHash) {
		Payload payload;

		try {
			payload = codec.decodeAndVerify(ticket);
			time.validateTimes(payload.times());
		}
		catch (IllegalArgumentException error) {
			throw new GuestSessionException("INVALID_START_TICKET");
		}

		if (!payload.requestId().equals(key)
				|| !MessageDigest.isEqual(payload.credentialHash(), credentialHash))
			throw new GuestSessionException("GUEST_CREDENTIAL_INVALID");
		if (!payload.serviceEpoch().equals(epoch))
			throw new GuestSessionException("SERVICE_EPOCH_MISMATCH");

		return payload;
	}

	public boolean currentEpoch(String value) {
		return epoch.toString().equals(value);
	}
}

