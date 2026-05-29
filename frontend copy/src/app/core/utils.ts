export function readError(err: unknown, fallback: string): string {
  if (typeof err === 'string') {
    return err;
  }
  if (err && typeof err === 'object' && 'error' in err) {
    const e = (err as { error: unknown }).error;
    if (typeof e === 'string') {
      return e;
    }
    if (e && typeof e === 'object' && 'message' in e) {
      return String((e as { message: string }).message);
    }
  }
  return fallback;
}

export function statusBadgeClass(status?: string): string {
  if (!status) {
    return 'badge-stage-warn';
  }
  const u = status.toUpperCase();
  if (u.includes('APPROV')) {
    return 'badge-stage-ok';
  }
  if (u.includes('REJECT')) {
    return 'badge-stage-bad';
  }
  return 'badge-stage-warn';
}

export function companyName(loan: { customer?: { companyName?: string } }): string {
  return loan.customer?.companyName ?? '—';
}
